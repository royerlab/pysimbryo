package pysimbryo;

import clearcl.ClearCL;
import clearcl.ClearCLContext;
import clearcl.ClearCLDevice;
import clearcl.ClearCLImage;
import clearcl.backend.ClearCLBackendInterface;
import clearcl.backend.ClearCLBackends;
import clearcl.viewer.ClearCLImageViewer;
import coremem.offheap.OffHeapMemory;
import coremem.util.Size;
import py4j.GatewayServer;
import simbryo.synthoscopy.microscope.lightsheet.drosophila.LightSheetMicroscopeSimulatorDrosophila;

public class PySimbryoServerApplication
{
  public ClearCLBackendInterface mClearCLBackend;
  public ClearCL mClearCL;
  public LightSheetMicroscopeSimulatorDrosophila mSimulator;
  public int mNumberOfDetectionArms;
  public int mNumberOfIlluminationArms;
  public ClearCLImageViewer mCameraImageViewer;

  public static void main(String[] args)
  {
    System.out.println("PySimbryo");
    PySimbryoServerApplication lApplication =
                                            new PySimbryoServerApplication(args);
    // app is now the gateway.entry_point

    lApplication.openCamerasViewers();
    lApplication.openControls();

    // Remove this line, this is meant for testing:
    lApplication.example();

    // Remove this line to prevent constant rendering:
    lApplication.startRenderLoop();

    GatewayServer server = new GatewayServer(lApplication);
    System.out.println("Server starting ...");
    server.start();
  }

  public PySimbryoServerApplication(String[] args)
  {
    startSimulator(args);
  }

  private void startSimulator(String[] args)
  {
    float lInitialDivisionTimePoint = 11f;

    mNumberOfDetectionArms = 1;
    mNumberOfIlluminationArms = 1;

    int lPhantomWidth = 320;
    int lPhantomHeight = lPhantomWidth;
    int lPhantomDepth = lPhantomWidth;

    int lMaxCameraResolution = 1024;

    mClearCLBackend = ClearCLBackends.getBestBackend();

    System.out.println("Backend: " + mClearCLBackend);

    mClearCL = new ClearCL(mClearCLBackend);

    System.out.println("OpenCL devices found: "
                       + mClearCL.getAllDevices());

    ClearCLDevice lFastestGPUDevice =
                                    mClearCL.getFastestGPUDeviceForImages();

    System.out.println("OpenCL device chosen: " + lFastestGPUDevice);

    ClearCLContext lContext = lFastestGPUDevice.createContext();

    mSimulator =
               new LightSheetMicroscopeSimulatorDrosophila(lContext,
                                                           mNumberOfDetectionArms,
                                                           mNumberOfIlluminationArms,
                                                           lMaxCameraResolution,
                                                           lInitialDivisionTimePoint,
                                                           lPhantomWidth,
                                                           lPhantomHeight,
                                                           lPhantomDepth);

  }

  public void openCamerasViewers()
  {

    mCameraImageViewer = mSimulator.openViewerForCameraImage(0);
    for (int i = 1; i < mNumberOfDetectionArms; i++)
      mCameraImageViewer = mSimulator.openViewerForCameraImage(i);

  }

  public void openOtherViewers()
  {
    mSimulator.openViewerForFluorescencePhantom();
    mSimulator.openViewerForScatteringPhantom();

    for (int i = 0; i < mNumberOfDetectionArms; i++)
      mSimulator.openViewerForLightMap(i);

  }

  public void openControls()
  {
    mSimulator.openViewerForControls();
  }

  public void example()
  {
    // This is how you access a lightsheet position:
    mSimulator.getLightSheet(0).setLightSheetPosition(0, 0, 0);

    // This is how you set the alpha angle:
    mSimulator.getLightSheet(0).setAlphaInRadians(0);

    // This is how you set the beta angle:
    mSimulator.getLightSheet(0).setBetaInRadians(0);

    // This is how to access a detection arm parameters:
    mSimulator.getDetectionOptics(0).setZFocusPosition(0);

    // This is how you render an image to the camera:
    mSimulator.render(true);

    // This is how you get a Camera Image:
    final ClearCLImage lCameraImage = mSimulator.getCameraImage(0);

    // This is how you get a float array for the image:
    float[] lArray = convertImageToFloatArray(lCameraImage);

    // This is how you get the width and similarly the height:
    lCameraImage.getWidth();

    // Testing:
    System.out.println("size of array:" + lArray.length);

  }

  public static float[] convertImageToFloatArray(ClearCLImage pImage)
  {
    int lNumberOfFloats = (int) (pImage.getSizeInBytes()
                                 / Size.of(Float.class));

    final OffHeapMemory lBuffer =
                                OffHeapMemory.allocateFloats(lNumberOfFloats);
    pImage.writeTo(lBuffer, true);

    float[] lFloatArray = new float[lNumberOfFloats];
    lBuffer.copyTo(lFloatArray);

    return lFloatArray;
  }

  public void startRenderLoop()
  {
    while (mCameraImageViewer.isShowing())
    {
      // lSimulator.simulationSteps(100, 1);
      mSimulator.render(true);
    }
  }

  public int addition(int first, int second)
  {
    return first + second;
  }

}
