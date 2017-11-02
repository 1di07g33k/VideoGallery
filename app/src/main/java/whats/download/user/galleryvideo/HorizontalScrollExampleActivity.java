package whats.download.user.galleryvideo;

import android.app.Activity;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.ThumbnailUtils;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.MediaController;
import android.widget.Toast;
import android.widget.VideoView;

import java.util.ArrayList;

public class HorizontalScrollExampleActivity extends Activity implements
        View.OnClickListener {
    LinearLayout myGallery;
    Cursor cursor;
    private ArrayList<VideoViewInfo> _videoRows;
    private VideoView mVideoView;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.gallaryview);

        myGallery = (LinearLayout) findViewById(R.id.mygallery);
        mVideoView = (VideoView) findViewById(R.id.videoView1);
        String targetPath = Environment.getExternalStorageDirectory()
                .getAbsolutePath();

        Toast.makeText(getApplicationContext(), targetPath, Toast.LENGTH_LONG)
                .show();
        loaddata();
        /*
         * File targetDirector = new File(targetPath);
         *
         * File[] files = targetDirector.listFiles(); for (File file : files) {
         * myGallery.addView(insertPhoto(file.getAbsolutePath())); }
         */
    }

    private void loaddata() {
        String[] thumbColumns = { MediaStore.Video.Thumbnails.DATA,
                MediaStore.Video.Thumbnails.VIDEO_ID };

        String[] mediaColumns = { MediaStore.Video.Media._ID,
                MediaStore.Video.Media.DATA, MediaStore.Video.Media.TITLE,
                MediaStore.Video.Media.MIME_TYPE };

        cursor = managedQuery(MediaStore.Video.Media.EXTERNAL_CONTENT_URI,
                mediaColumns, null, null, null);

        ArrayList<VideoViewInfo> videoRows = new ArrayList<VideoViewInfo>();
        if (cursor.moveToFirst()) {
            do {

                VideoViewInfo newVVI = new VideoViewInfo();
                int id = cursor.getInt(cursor
                        .getColumnIndex(MediaStore.Video.Media._ID));
                Cursor thumbCursor = managedQuery(
                        MediaStore.Video.Thumbnails.EXTERNAL_CONTENT_URI,
                        thumbColumns, MediaStore.Video.Thumbnails.VIDEO_ID
                                + "=" + id, null, null);
                if (thumbCursor.moveToFirst()) {
                    newVVI.thumbPath = thumbCursor.getString(thumbCursor
                            .getColumnIndex(MediaStore.Video.Thumbnails.DATA));
                    Log.v("", newVVI.thumbPath);
                }

                newVVI.filePath = cursor.getString(cursor
                        .getColumnIndexOrThrow(MediaStore.Video.Media.DATA));
                newVVI.title = cursor.getString(cursor
                        .getColumnIndexOrThrow(MediaStore.Video.Media.TITLE));
                Log.v("", newVVI.title);
                newVVI.mimeType = cursor
                        .getString(cursor
                                .getColumnIndexOrThrow(MediaStore.Video.Media.MIME_TYPE));
                Log.v("", newVVI.mimeType);
                videoRows.add(newVVI);
            } while (cursor.moveToNext());
        }
        _videoRows = videoRows;
        myGallery.addView(insertPhoto(videoRows));
    }

    View insertPhoto(ArrayList<VideoViewInfo> videoRows) {
        LinearLayout layout = new LinearLayout(getApplicationContext());
        layout.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, 250));
        layout.setGravity(Gravity.CENTER);
        for (int i = 0; i < videoRows.size(); i++) {

            Bitmap bmThumbnail;

            bmThumbnail = ThumbnailUtils.createVideoThumbnail(
                    videoRows.get(i).filePath, MediaStore.Video.Thumbnails.MICRO_KIND);

            ImageView imageView = new ImageView(getApplicationContext());
            imageView.setLayoutParams(new ViewGroup.LayoutParams(220, 220));
            imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
            // imageView.setBackgroundResource(R.drawable.canada);
            imageView.setImageBitmap(bmThumbnail);
            imageView.setPadding(20, 40, 20, 40);
            imageView.setTag(videoRows.get(i).filePath);

            imageView.setOnClickListener(this);
            layout.addView(imageView);

        }

        return layout;
    }

    @Override
    public void onClick(View v) {
        if (mVideoView.isPlaying() == true) {
            mVideoView.stopPlayback();
        }
        Toast.makeText(getApplicationContext(), v.getTag().toString(),
                Toast.LENGTH_SHORT).show();

        String filepath = v.getTag().toString();
        play(filepath);

    }

    public void play(String index) {

        Uri videoUri = Uri.parse(index);
        MediaController mc = new MediaController(this);

        mc.setAnchorView(mVideoView);
        mVideoView.setMediaController(mc);
        mVideoView.setVideoURI(videoUri);
        mc.setMediaPlayer(mVideoView);
        mVideoView.requestFocus();
        mVideoView.start();
    }

    public Bitmap decodeSampledBitmapFromUri(String path, int reqWidth,
                                             int reqHeight) {
        Bitmap bm = null;

        // First decode with inJustDecodeBounds=true to check dimensions
        final BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(path, options);

        // Calculate inSampleSize
        options.inSampleSize = calculateInSampleSize(options, reqWidth,
                reqHeight);

        // Decode bitmap with inSampleSize set
        options.inJustDecodeBounds = false;
        bm = BitmapFactory.decodeFile(path, options);

        return bm;
    }

    public int calculateInSampleSize(

            BitmapFactory.Options options, int reqWidth, int reqHeight) {
        // Raw height and width of image
        final int height = options.outHeight;
        final int width = options.outWidth;
        int inSampleSize = 1;

        if (height > reqHeight || width > reqWidth) {
            if (width > height) {
                inSampleSize = Math.round((float) height / (float) reqHeight);
            } else {
                inSampleSize = Math.round((float) width / (float) reqWidth);
            }
        }

        return inSampleSize;
    }

}