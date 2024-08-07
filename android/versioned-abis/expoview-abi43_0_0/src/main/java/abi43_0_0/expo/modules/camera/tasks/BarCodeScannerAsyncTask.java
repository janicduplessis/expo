package abi43_0_0.expo.modules.camera.tasks;

import abi43_0_0.expo.modules.interfaces.barcodescanner.BarCodeScannerInterface;
import abi43_0_0.expo.modules.interfaces.barcodescanner.BarCodeScannerResult;

public class BarCodeScannerAsyncTask extends android.os.AsyncTask<Void, Void, BarCodeScannerResult> {
  private final BarCodeScannerInterface mBarCodeScanner;
  private byte[] mImageData;
  private int mWidth;
  private int mHeight;
  private int mRotation;
  private BarCodeScannerAsyncTaskDelegate mDelegate;

  public BarCodeScannerAsyncTask(
      BarCodeScannerAsyncTaskDelegate delegate,
      BarCodeScannerInterface barCodeScanner,
      byte[] imageData,
      int width,
      int height,
      int rotation
  ) {
    mImageData = imageData;
    mWidth = width;
    mHeight = height;
    mDelegate = delegate;
    mBarCodeScanner = barCodeScanner;
    mRotation = rotation;
  }

  @Override
  protected BarCodeScannerResult doInBackground(Void... ignored) {
    if (isCancelled() || mDelegate == null) {
      return null;
    }

    return mBarCodeScanner.scan(mImageData, mWidth, mHeight, mRotation);
  }

  @Override
  protected void onPostExecute(BarCodeScannerResult result) {
    super.onPostExecute(result);
    if (result != null) {
      mDelegate.onBarCodeScanned(result);
    }
    mDelegate.onBarCodeScanningTaskCompleted();
  }
}
