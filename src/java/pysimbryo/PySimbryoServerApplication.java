package pysimbryo;

import py4j.GatewayServer;

public class PySimbryoServerApplication
{

  public int addition(int first, int second)
  {
    return first + second;
  }

  public static void main(String[] args)
  {
    System.out.println("PySimbryo");
    PySimbryoServerApplication app = new PySimbryoServerApplication();
    // app is now the gateway.entry_point
    GatewayServer server = new GatewayServer(app);
    System.out.println("Server starting ...");
    server.start();
  }
}
