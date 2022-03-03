package com.example.asynctask;
import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;


public class MainActivity extends AppCompatActivity {
    private static final int WRITE_REQUEST_CODE = 100;
    String[] files = {
            "https://static.googleusercontent.com/media/research.google.com/en//pubs/archive/45530.pdf",
            "https://hadoop.apache.org/docs/r1.2.1/hdfs_design.pdf",
            "https://pages.databricks.com/rs/094-YMS-629/images/LearningSpark2.0.pdf",
            "https://docs.aws.amazon.com/wellarchitected/latest/machine-learning-lens/wellarchitected-machine-learning-lens.pdf",
            "https://developers.snowflake.com/wp-content/uploads/2020/09/SNO-eBook-7-Reference-Architectures-for-Application-Builders-MachineLearning-DataScience.pdf"
    };
    Button buttonDownload;
    TextView fileUrls;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        buttonDownload = findViewById(R.id.button);
        fileUrls = findViewById(R.id.editText);

        for(int i = 0; i< files.length; i++ ){
            String fileTail = files[i].substring(files[i].lastIndexOf('/') + 1, files[i].length());
            fileUrls.append("PDF "+ String.valueOf(i+1) + " location: .../" + fileTail + "\n");
        }

        buttonDownload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String[] permissions = {Manifest.permission.WRITE_EXTERNAL_STORAGE};
                requestPermissions(permissions, WRITE_REQUEST_CODE);
            }
        });

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case WRITE_REQUEST_CODE:
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    DownloadAsyncTask object = new DownloadAsyncTask(this);
                    object.execute(files);
                } else {
                    //Denied.
                }
                break;
        }
    }
}