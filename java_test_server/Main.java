import java.net.ServerSocket;
import java.net.Socket;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.security.MessageDigest;
import java.util.Base64;
import java.util.Arrays;
import java.io.ByteArrayOutputStream;
import java.nio.charset.StandardCharsets;

public class Main {
  public static void main(String[] args) throws Exception {
    final ServerSocket server = new ServerSocket(9999);
    System.out.println("Server has started");
    final Socket client = server.accept();
    System.out.println("client has connected");

    final InputStream in = client.getInputStream();
    final OutputStream out = client.getOutputStream();
    final String data = new Scanner(in, "UTF-8").useDelimiter("\\r\\n\\r\\n").next();
    System.out.print("\n\n" + data);
    final Matcher get = Pattern.compile("^GET").matcher(data);

    if(get.find()) {
      final String magicNumber = "258EAFA5-E914-47DA-95CA-C5AB0DC85B11";
      final Matcher match = Pattern.compile("sec-websocket-key: (.*)").matcher(data);
      match.find();
      final String key = match.group(1);
      System.out.println("\n\nkey: " + key);
      final String key_and_magicNumber = key + magicNumber;
      System.out.println("knm: " + key_and_magicNumber);
      final byte[] sha1 = MessageDigest.getInstance("SHA-1").digest(key_and_magicNumber.getBytes("UTF-8"));
      final String b64 = Base64.getEncoder().encodeToString(sha1);
      System.out.println("b64: " + b64);
      // final String response = (
          // "HTTP/1.1 101 Switching Protocols\r\n" +
          // "Connection: Upgrade\r\n" +
          // "Upgrade: websocket\r\n" +
          // "Sec-WebSocket-Accept: " + b64 +
          // "\r\n\r\n");
      final String response = (
          "HTTP/1.1 101 Switching Protocols\r\n" + 
          "upgrade: websocket\r\n" + 
          "sec-websocket-accept: " + b64 + "\r\n" +
          "connection: upgrade\r\n" +
          "date: Sun, 28 Oct 2018 21:40:33 GMT" +
          "\r\n\r\n");
      System.out.println("\n" + response);
      final byte[] respAsBytes = response.getBytes("UTF-8");
      out.write(respAsBytes, 0, respAsBytes.length);

      final ByteArrayOutputStream baos = new ByteArrayOutputStream();
      int nRead;
      byte[] buffer = new byte[64];
      while((nRead = in.read(buffer)) != -1) {
        // System.out.println(Arrays.toString(buffer));
        baos.write(buffer, 0, nRead);
        System.out.println(Arrays.toString(baos.toByteArray()));
        byte[] ba = baos.toByteArray();
        if(ba.length < 10) {
          continue;
        }
        for(byte b: ba) {
          System.out.println("byte: " + (b & 0xFF));
        }

        boolean end = false;
        int offset = 0;
        while(!end) {

          int packetLength = ba[offset + 1] & 0x7F; 
          System.out.println("packet length: " + packetLength);

          byte[] mask = new byte[4];
          mask[0] = ba[offset + 2];
          mask[1] = ba[offset + 3];
          mask[2] = ba[offset + 4];
          mask[3] = ba[offset + 5];

          byte[] decoded = new byte[packetLength];
          for(int i = 0; i < decoded.length; i++) {
            decoded[i] = (byte)(ba[offset + i + 6] ^ mask[i % 4]);
          }

          System.out.println(Arrays.toString(decoded));
          System.out.println(new String(decoded, StandardCharsets.UTF_8));

          offset += packetLength + 6;
          if(offset >= ba.length) {
            end = true;
          }
        }
      }
    } else {

    }

  }
}
