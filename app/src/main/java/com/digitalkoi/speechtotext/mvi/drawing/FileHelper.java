package com.digitalkoi.speechtotext.mvi.drawing;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import com.rm.freedrawview.FreeDrawSerializableState;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

/**
 * @author Taras Zhupnyk (akka DigitalKoi) on 14/03/18.
 */

class FileHelper {
  private static final String FILE_NAME = "draw_state.ser";

  static void saveStateIntoFile(
      final Context context, final FreeDrawSerializableState state,
      final StateSaveInterface listener) {

    if (context != null && state != null) {

      new Thread(new StateSaveRunnable(context, listener, state)).start();
    } else {

      if (listener != null) {
        listener.onStateSaveError();
      }
    }
  }

  static void getSavedStoreFromFile(
      final Context context, StateExtractorInterface listener) {

    if (context != null) {

      new Thread(new StateExtractorRunnable(context, listener)).start();
    } else {

      if (listener != null) {
        listener.onStateExtractionError();
      }
    }
  }

  static void deleteSavedStateFile(Context context) {

    if (context != null) {

      FileOutputStream fos = null;
      try {
        fos = context.openFileOutput(FILE_NAME, Context.MODE_PRIVATE);
        ObjectOutputStream os = new ObjectOutputStream(fos);
        os.close();
        fos.close();
      } catch (Exception e) {
        e.printStackTrace();

        if (fos != null) {

          try {
            fos.close();
          } catch (Exception e1) {
            e1.printStackTrace();
          }
        }
      }
    }
  }


  // Runnable that extracts the FreeDrawSerializableState from a file
  private static class StateExtractorRunnable implements Runnable {

    private Context mContext;
    private StateExtractorInterface mListener;

    StateExtractorRunnable(Context context, StateExtractorInterface listener) {
      mContext = context;
      mListener = listener;
    }

    @Override
    public void run() {
      FileInputStream fis = null;
      try {

        fis = mContext.openFileInput(FILE_NAME);
        ObjectInputStream is = new ObjectInputStream(fis);

        final FreeDrawSerializableState state = (FreeDrawSerializableState) is.readObject();

        fis.close();
        is.close();

        if (mListener != null) {
          runOnUiThread(() -> mListener.onStateExtracted(state));
        }
      } catch (Exception e) {
        e.printStackTrace();

        if (fis != null) {

          try {
            fis.close();
          } catch (Exception e1) {
            e1.printStackTrace();
          }
        }

        if (mListener != null) {

          runOnUiThread(() -> mListener.onStateExtractionError());
        }
      }
    }
  }


  // Runnable that save a FreeDrawSerializableState inside a file
  private static class StateSaveRunnable implements Runnable {

    private final Context mContext;
    private final StateSaveInterface mListener;
    private final FreeDrawSerializableState mState;

    StateSaveRunnable(
        Context context, StateSaveInterface listener, FreeDrawSerializableState state) {

      mContext = context;
      mListener = listener;
      mState = state;
    }

    @Override
    public void run() {
      FileOutputStream fos = null;
      try {
        fos = mContext.openFileOutput(FILE_NAME, Context.MODE_PRIVATE);
        ObjectOutputStream os = new ObjectOutputStream(fos);
        os.writeObject(mState);
        os.flush();
        fos.flush();
        os.close();
        fos.close();

        if (mListener != null) {
          runOnUiThread(mListener::onStateSaved);
        }
      } catch (Exception e) {
        e.printStackTrace();

        if (fos != null) {

          try {
            fos.close();
          } catch (Exception e1) {
            e1.printStackTrace();
          }
        }

        runOnUiThread(mListener::onStateSaveError);
      }
    }
  }


  // Listener for file creation
  public interface StateSaveInterface {
    void onStateSaved();

    void onStateSaveError();
  }

  // Listener for file data extraction
  public interface StateExtractorInterface {
    void onStateExtracted(FreeDrawSerializableState state);

    void onStateExtractionError();
  }


  // Shortcut method to run on uiThread a runnable
  private static void runOnUiThread(Runnable runnable) {

    new Handler(Looper.getMainLooper()).post(runnable);
  }
}
