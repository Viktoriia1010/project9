import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.channels.FileChannel;
import java.nio.charset.CharsetDecoder;
import java.nio.charset.CharsetEncoder;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;

class File22 implements Runnable {
    public String start;     // откуда читаем
    public String new_file;  // куда записываем

    public File22(String start, String new_file) {
        this.start = start;
        this.new_file = new_file;
    }

    public void run() {
        CharsetDecoder decoder = StandardCharsets.UTF_16.newDecoder();
        CharsetEncoder encoder = StandardCharsets.UTF_16.newEncoder();

        try (FileChannel inChannel = FileChannel.open(Path.of(start), StandardOpenOption.READ);
             FileChannel outChannel = FileChannel.open(
                     Path.of(new_file),
                     StandardOpenOption.CREATE,
                     StandardOpenOption.WRITE,
                     StandardOpenOption.TRUNCATE_EXISTING
             )) {

            ByteBuffer readBuffer = ByteBuffer.allocate(1024);
            while (inChannel.read(readBuffer) != -1) {
                readBuffer.flip();
                CharBuffer charBuffer = decoder.decode(readBuffer);
                ByteBuffer writeBuffer = encoder.encode(charBuffer);
                while (writeBuffer.hasRemaining()) {
                    outChannel.write(writeBuffer);
                }
                readBuffer.clear();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

public class Main {
    public static void main(String[] args) throws InterruptedException {
        String file1 = "C:\\Users\\Привет\\OneDrive\\Рабочий стол\\данные\\заказ.txt";
        String file2 = "C:\\Users\\Привет\\OneDrive\\Рабочий стол\\данные\\Tovar2.txt";

        String copy1 = "C:\\Users\\Привет\\OneDrive\\Рабочий стол\\данные\\заказ_copy.txt";
        String copy2 = "C:\\Users\\Привет\\OneDrive\\Рабочий стол\\данные\\Tovar2_copy.txt";

        long starttime = System.currentTimeMillis();
        new File22(file1, copy1).run();
        new File22(file2, copy2).run();

        long endtime = System.currentTimeMillis(); // таймер
        System.out.println("время последовательно " + (endtime - starttime));

        long start = System.currentTimeMillis();

        Thread t1 = new Thread(new File22(file1, copy1));
        Thread t2 = new Thread(new File22(file2, copy2));

        t1.start();
        t2.start();

        t1.join();
        t2.join();

        long endPar = System.currentTimeMillis(); // тип такой так как значения времени огромные
        System.out.println("время параллельно: " + (endPar - start));
    }
}