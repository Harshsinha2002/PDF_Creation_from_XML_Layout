package com.example.pdf_creation;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.pm.PackageManager;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.pdf.PdfDocument;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import javax.xml.transform.OutputKeys;

public class MainActivity extends AppCompatActivity {

    Button btnCreatePDF;
    Button btnXMLtoPDF;
    final static int WRITE_EXTERNAL_STORAGE_REQUEST_CODE = 1232;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        btnCreatePDF = findViewById(R.id.btnCreatePdf);
        btnCreatePDF.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                askPermissions();
                createPDF();
            }
        });

        btnXMLtoPDF = findViewById(R.id.btnXMLToPDF);
        btnXMLtoPDF.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                askPermissions();
                convertXmlToPdf();
            }
        });
    }

    private void createPDF()
    {
        PdfDocument document = new PdfDocument();
        PdfDocument.PageInfo pageInfo = new PdfDocument.PageInfo.Builder(1080, 1920, 1).create();
        PdfDocument.Page page = document.startPage(pageInfo);

        Canvas canvas = page.getCanvas();

        Paint paint = new Paint();
        paint.setColor(Color.RED);
        paint.setTextSize(42);

        String text = "Hello, World";
        float x = 500;
        float y = 900;

        canvas.drawText(text, x, y, paint);
        document.finishPage(page);

        File downloadsDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);
        String fileName = "example.pdf";
        File file = new File(downloadsDir, fileName);

        try
        {
            FileOutputStream fos = new FileOutputStream(file);
            document.writeTo(fos);
            document.close();
            fos.close();
            Toast.makeText(this, "Written Successfully!!!", Toast.LENGTH_SHORT).show();
        }

        catch (FileNotFoundException e)
        {
            Log.d("mylog", "Error while writing " + e.toString());
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }


    private void convertXmlToPdf()
    {
        // Inflate the XML layout file
        View view = LayoutInflater.from(this).inflate(R.layout.activity_your_analysis_report_page, null);
        DisplayMetrics displayMetrics = new DisplayMetrics();

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R)
        {
            this.getDisplay().getRealMetrics(displayMetrics);
        }
        else
        {
            this.getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        }

        view.measure(View.MeasureSpec.makeMeasureSpec(displayMetrics.widthPixels, View.MeasureSpec.EXACTLY),
                View.MeasureSpec.makeMeasureSpec(displayMetrics.heightPixels, View.MeasureSpec.EXACTLY));


        view.layout(0,0,displayMetrics.widthPixels, displayMetrics.heightPixels);
        // Create a new PdfDocument instance
        PdfDocument document = new PdfDocument();

        // Obtain the width and height of the view
        int viewWidth = view.getMeasuredWidth();
        int viewHeight = view.getMeasuredHeight();
        // Create a PageInfo object specifying the page attributes
        PdfDocument.PageInfo pageInfo = new PdfDocument.PageInfo.Builder(viewWidth, viewHeight, 1).create();

        // Start a new page
        PdfDocument.Page page = document.startPage(pageInfo);

        // Get the Canvas object to draw on the page
        Canvas canvas = page.getCanvas();

        // Create a Paint object for styling the view
        Paint paint = new Paint();
        paint.setColor(Color.WHITE);

        // Draw the view on the canvas
        view.draw(canvas);

        // Finish the page
        document.finishPage(page);

        // Specify the path and filename of the output PDF file
        File downloadsDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);
        String fileName = "Reports.pdf";
        File filePath = new File(downloadsDir, fileName);

        try {
            // Save the document to a file
            FileOutputStream fos = new FileOutputStream(filePath);
            document.writeTo(fos);
            document.close();
            fos.close();
            // PDF conversion successful
            Toast.makeText(this, "XML to PDF Conversion Successful", Toast.LENGTH_LONG).show();
        } catch (IOException e) {
            e.printStackTrace();
            // Error occurred while converting to PDF
        }
    }

    private void askPermissions()
    {
        if (ContextCompat.checkSelfPermission(MainActivity.this, android.Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED)
        {
            ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, WRITE_EXTERNAL_STORAGE_REQUEST_CODE);
        }
    }




    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if(requestCode == WRITE_EXTERNAL_STORAGE_REQUEST_CODE && grantResults.length >0 && grantResults[0] == PackageManager.PERMISSION_GRANTED)
            {
                createPDF();
                convertXmlToPdf();
            }

        else
        {
            Toast.makeText(MainActivity.this, "App Permission Denied!! Check Settings", Toast.LENGTH_LONG);
        }
    }
}