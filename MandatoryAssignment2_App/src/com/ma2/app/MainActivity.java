package com.ma2.app;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.net.Socket;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface.OnClickListener;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

public class MainActivity extends Activity {
	
	private String ip;
	
	private String[] pictures = new String[]
			{
			"ic_launcher",
			"a0979",
			"a0982",
			"soup_nazi"			
			};

	private int[] mImages = new int[] {
			R.drawable.ic_launcher,
			R.drawable.a0979,
			R.drawable.a0982,
			R.drawable.soup_nazi
			//				R.drawable.ulm
	};
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		String ip;
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setPositiveButton(R.string.OK, null);
		EditText tv = new EditText(this);
		tv.setText("10.25.231.246");
		tv.setText("10.25.239.235");
		builder.setView(tv);
		builder.setPositiveButton("OK", onClick(builder, tv));
		builder.show();
		
		ViewPager viewPager = (ViewPager) findViewById(R.id.picture);
		ImagePagerAdapter adapter = new ImagePagerAdapter();
		viewPager.setAdapter(adapter);
	}

    public OnClickListener onClick(Builder builder, EditText tv) {
        ip = tv.getText().toString();
        return null;
    }
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		menu.add("Send this picture");
		return true;
	}

	@Override 
	public boolean onOptionsItemSelected(MenuItem item){
		if(item.getItemId() == 0){ // send picture
			ViewPager viewPager = (ViewPager) findViewById(R.id.picture);
			int i = viewPager.getCurrentItem();			
			new SendPicture().execute(i);
		}
		//		mImages[i];
		return super.onOptionsItemSelected(item);
	}

	public void showDialog(boolean b, String error) {

		// 1. Instantiate an AlertDialog.Builder with its constructor
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		//add OK button
		builder.setPositiveButton(R.string.OK, null);
		if(b == true){
			// 2. Chain together various setter methods to set the dialog characteristics
			builder.setMessage(R.string.sent).setTitle(R.string.success);
		}else{
			// 2. Chain together various setter methods to set the dialog characteristics
			builder.setMessage(error).setTitle(R.string.error_title);
		}
		// 3. Get the AlertDialog from create()
		AlertDialog dialog = builder.create();
		dialog.show();

	}

	private class SendPicture extends AsyncTask<Integer, Integer, Boolean>{
		ProgressDialog progress;
		
		@Override
		protected Boolean doInBackground(Integer... picture) {
			// TODO Auto-generated method stub
			try{
				publishProgress(0);
				Bitmap bitmap = BitmapFactory.decodeResource(getResources(), mImages[picture[0]]);
				ByteArrayOutputStream stream = new ByteArrayOutputStream();
				if(!bitmap.compress(Bitmap.CompressFormat.JPEG, 50, stream)){
					return false;
				}
				byte [] byte_arr = stream.toByteArray();
				//10.25.239.235
				if(ip == null){
					return false;
				}
				Socket socket = new Socket(ip,8000);
				DataOutputStream out = new DataOutputStream(socket.getOutputStream());
				int sent = 0;
				out.writeInt(byte_arr.length);
//				out.writeUTF(pictures[picture[0]]);
				
				while(sent < byte_arr.length){
					System.out.println(sent + "android");
					if(sent > 1024){
						out.write(byte_arr, sent, 1024);
						sent = sent + 1024;
					}else{
						out.write(byte_arr, sent, byte_arr.length - sent);
						sent = byte_arr.length;
					}
					
					publishProgress((sent*100)/byte_arr.length);
				}
				out.close();
				socket.close();
//				out.write(byte_arr);
				publishProgress(100);
			}catch(ArrayIndexOutOfBoundsException ex){
				return true;
			}
			catch(Exception e){
				System.out.println(e.toString());
				return false;
			}

			return true;
		}

		@Override
		protected void onPreExecute(){
			progress = ProgressDialog.show(MainActivity.this, "Sending picture", null, true);
		}
		
		@Override
		protected void onPostExecute(Boolean result){
			progress.dismiss();
			showDialog(result,null);
		}
	}

	private class ImagePagerAdapter extends PagerAdapter {

		@Override
		public int getCount() {
			return mImages.length;
		}

		@Override
		public boolean isViewFromObject(View view, Object object) {
			return view == ((ImageView) object);
		}

		@Override
		public void startUpdate(ViewGroup container){
			int i= ((ViewPager) container).getCurrentItem();
			TextView tv = (TextView) findViewById(R.id.picName);
			tv.setText(pictures[i]);
		}

		@Override
		public Object instantiateItem(ViewGroup container, int position) {
			Context context = MainActivity.this;
			ImageView imageView = new ImageView(context);
			int padding = 0;
			//			int padding = 5;
			imageView.setPadding(padding, padding, padding, padding);
			imageView.setScaleType(ImageView.ScaleType.CENTER_INSIDE);
			imageView.setImageBitmap(decodeSampledBitmapFromResource(getResources(), mImages[position], 2048, 2048));
			((ViewPager) container).addView(imageView, 0);

			return imageView;
		}

		@Override
		public void destroyItem(ViewGroup container, int position, Object object) {
			((ViewPager) container).removeView((ImageView) object);
		}

		//In case the image is too large
		public Bitmap decodeSampledBitmapFromResource(Resources res, int resId,int reqWidth, int reqHeight) {

			// First decode with inJustDecodeBounds=true to check dimensions
			final BitmapFactory.Options options = new BitmapFactory.Options();
			options.inJustDecodeBounds = true;
			BitmapFactory.decodeResource(res, resId, options);

			// Calculate inSampleSize
			options.inSampleSize = calculateInSampleSize(options, reqWidth, reqHeight);

			// Decode bitmap with inSampleSize set
			options.inJustDecodeBounds = false;
			return BitmapFactory.decodeResource(res, resId, options);
		}

		public int calculateInSampleSize(
				BitmapFactory.Options options, int reqWidth, int reqHeight) {
			// Raw height and width of image
			final int height = options.outHeight;
			final int width = options.outWidth;
			int inSampleSize = 1;

			if (height > reqHeight || width > reqWidth) {

				// Calculate ratios of height and width to requested height and width
				final int heightRatio = (int)Math.ceil((float) height / (float) reqHeight);
				final int widthRatio = (int)Math.ceil((float) width / (float) reqWidth);

				// Choose the smallest ratio as inSampleSize value, this will guarantee
				// a final image with both dimensions larger than or equal to the
				// requested height and width.
				inSampleSize = heightRatio > widthRatio ? heightRatio : widthRatio;
			}

			return inSampleSize;
		}
	}




}
