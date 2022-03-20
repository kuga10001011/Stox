package org.kuga.stox;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    public void startTrading(View view) {
        Intent intent = new Intent(this, TradingScreen.class);
        EditText inputAPIKeyBox = findViewById(R.id.input_api_key_edit_text);
        EditText outputAPIKeyBox = findViewById(R.id.output_api_key_edit_text);
        String inputAPIKey = inputAPIKeyBox.getText().toString();
        String outputAPIKey = outputAPIKeyBox.getText().toString();
        intent.putExtra("INPUT_API_KEY", inputAPIKey);
        intent.putExtra("OUTPUT_API_KEY", outputAPIKey);
        if (inputAPIKey.length() > 0 && outputAPIKey.length() > 0) {
            startActivity(intent);
        }
        else {
            //open popup window to handle invalid keys
        }
    }
}