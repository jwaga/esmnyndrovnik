package pl.eastsidemandala.nyndrovnik;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.text.Html;
import android.view.View;
import android.widget.TextView;


public class AboutActivity extends FragmentActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);
        TextView text = (TextView)findViewById(R.id.about_textview);
        if (text != null) {
            text.setText(Html.fromHtml(getResources().getString(R.string.about_esm)));
        }

    }


    public void visitWebsite(View view) {
        startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(getString(R.string.website))) {
        });
    }


}
