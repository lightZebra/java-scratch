package jetty;

import java.io.IOException;
import java.io.OutputStream;
import java.net.Socket;
import java.util.Collections;
import java.util.Scanner;
import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import okhttp3.OkHttpClient;
import okhttp3.Request.Builder;
import okhttp3.Response;
import org.eclipse.jetty.server.Request;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.ServerConnector;
import org.eclipse.jetty.server.handler.AbstractHandler;
import org.eclipse.jetty.util.IO;

public class SimpleServer {

  public static void main(String[] args) throws Exception {
    Server server = new Server(8080);
    server.setHandler(new MyHandler());
    server.getBean(ServerConnector.class).setIdleTimeout(2000);

    try {
      server.start();

      final String url = "http://localhost:8080";
//      final String url = "http://192.168.0.100:3333/export/token/lego_test__non_organic_in_app_events_2020-07-06_2020-07-06_ZJKISDYCFPEHXQU";

//      showBytes("127.0.0.1", 8154, "huge-stream-with-error");
//      System.out.println();
      showBytes("127.0.0.1", 8080, "");
//      callOkHttp(new OkHttpClient(), new Builder().url(url).build());
//      call();

//      try (Socket s = new Socket("127.0.0.1", 8080)) {
//        s.getOutputStream()
//            .write("GET / HTTP/1.1\r\nHost: localhost\r\nConnection: close\r\n\r\n".getBytes());
//        s.getOutputStream().flush();
//        IO.copy(s.getInputStream(), System.out);
//      }
    } finally {
      new Scanner(System.in).nextLine();
      server.stop();
    }
  }

  private static void showBytes(String host, int port, String path) throws IOException {
    try (Socket s = new Socket(host, port)) {
      s.getOutputStream()
          .write(("GET /" + path + " HTTP/1.1\r\nHost: localhost\r\nConnection: keep-alive\r\n\r\n")
              .getBytes());
      s.getOutputStream().flush();
      IO.copy(s.getInputStream(), new OutputStream() {
        private int count = 0;

        @Override
        public void write(int b) throws IOException {
          if (b >= '0') {
            System.out.printf("%5s", (char) b);
          } else {
            System.out.printf("%5s", b);
          }
          if (++count % 20 == 0) {
            System.out.println();
          }
        }
      });
    }
  }

  private static void callOkHttp(OkHttpClient client, okhttp3.Request request) throws IOException {
    try (Response response = client.newCall(request).execute()) {
      System.out.println("response: " + response.body().string());
    }
  }

  private static String generateString(String pattern, int repeat) {
    return String.join("", Collections.nCopies(repeat, pattern));
  }

  public static class MyHandler extends AbstractHandler {

    public void handle(String target,
        Request baseRequest,
        HttpServletRequest request,
        HttpServletResponse response) throws IOException,
        ServletException {
      baseRequest.setHandled(true);
      response.setContentType("text/plain; charset=utf-8");
      response.setHeader("Content-Disposition", "attachment; filename=myFile.csv");
      response.setHeader("Transfer-Encoding", "chunked");
      response.setStatus(HttpServletResponse.SC_OK);

      ServletOutputStream out = response.getOutputStream();

      try {
        for (int i = 0; i < 100; i++) {
          if (i == 10) {
            break;
//            throw new OutOfMemoryError("test");
          }
//        System.out.println("step: " + i);
//        out.println(generateString("output" + i, 10000));
          out.println("output: " + i);
          out.flush();
          try {
            Thread.sleep(1000);
          } catch (InterruptedException e) {
            e.printStackTrace();
          }
        }
      } finally {
//        out.close();
      }
//      out.println("How now brown cow");
//      out.flush();
//      out.println("Now is the winter of our discontent");
//      out.flush();
//      out.println("The moon is blue to a fish in love");
    }
  }
}