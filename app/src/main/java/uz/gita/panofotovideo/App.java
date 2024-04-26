package uz.gita.panofotovideo;

import android.app.Application;
import android.net.Uri;

/**
 * Author: Zukhriddin Kamolov
 * Date: 26-Apr-24, 5:42 PM.
 * Project: PanoramaViewer
 */

public class App extends Application {

    static public Uri mPickMediaUri = null;
    static public Uri mAssetMediaUri = null;

    static public String mMediaLink = "https://github.com/Zulfiddinovich/Temp/blob/main/Cagliari%203.jpg?raw=true";

    /**
     * 1 - local (from assets and storage)
     * 2 - remote (with url)
     * */
    static public int PLAY_MEDIA_SOURCE = 2;
}
