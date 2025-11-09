package com.example.activateotp;

import android.os.Bundle;
import android.os.CountDownTimer;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.List;

public class OTPActivity extends AppCompatActivity {

    private EditText et1, et2, et3, et4, et5, et6;
    private Button btnVerify;
    private TextView tvError, tvResend;
    private ProgressBar progress;
    private List<EditText> fields;
    private CountDownTimer resendTimer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_verify_otp);

        // Khởi tạo view
        et1 = findViewById(R.id.et1);
        et2 = findViewById(R.id.et2);
        et3 = findViewById(R.id.et3);
        et4 = findViewById(R.id.et4);
        et5 = findViewById(R.id.et5);
        et6 = findViewById(R.id.et6);
        btnVerify = findViewById(R.id.btnVerify);
        tvError = findViewById(R.id.tvError);
        tvResend = findViewById(R.id.tvResend);
        progress = findViewById(R.id.progress);

        fields = new ArrayList<>();
        fields.add(et1);
        fields.add(et2);
        fields.add(et3);
        fields.add(et4);
        fields.add(et5);
        fields.add(et6);

        setupOtpFields();

        btnVerify.setOnClickListener(v -> {
            StringBuilder codeBuilder = new StringBuilder();
            for (EditText field : fields) codeBuilder.append(field.getText().toString());
            String code = codeBuilder.toString();

            if (code.length() != 6) {
                tvError.setVisibility(View.VISIBLE);
                tvError.setText(getString(R.string.otp_error_fill));
                return;
            }
            tvError.setVisibility(View.GONE);
            verifyOtp(code);
        });

        tvResend.setOnClickListener(v -> {
            if (tvResend.isEnabled()) {
                startResendCountdown();
                // Gọi API resend OTP ở đây (nếu có)
                Toast.makeText(this, getString(R.string.resend_sent), Toast.LENGTH_SHORT).show();
            }
        });

        startResendCountdown();
    }

    private void verifyOtp(String code) {
        progress.setVisibility(View.VISIBLE);
        progress.postDelayed(() -> {
            progress.setVisibility(View.GONE);
            if ("123456".equals(code)) {
                Toast.makeText(this, getString(R.string.otp_success), Toast.LENGTH_LONG).show();
                finish();
            } else {
                tvError.setVisibility(View.VISIBLE);
                tvError.setText(getString(R.string.otp_wrong));
            }
        }, 1000);
    }

    private void setupOtpFields() {
        for (int i = 0; i < fields.size(); i++) {
            EditText et = fields.get(i);
            final int index = i;
            et.addTextChangedListener(new TextWatcher() {
                @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
                @Override public void onTextChanged(CharSequence s, int start, int before, int count) {}
                @Override public void afterTextChanged(Editable s) {
                    if (s.length() == 1 && index < fields.size() - 1)
                        fields.get(index + 1).requestFocus();
                    else if (s.length() == 0 && index > 0)
                        fields.get(index - 1).requestFocus();
                }
            });
        }
    }

    private void startResendCountdown() {
        if (resendTimer != null) resendTimer.cancel();
        tvResend.setEnabled(false);
        resendTimer = new CountDownTimer(30000, 1000) {
            @Override public void onTick(long millisUntilFinished) {
                tvResend.setText(getString(R.string.resend_label) + " (" + millisUntilFinished / 1000 + "s)");
            }
            @Override public void onFinish() {
                tvResend.setEnabled(true);
                tvResend.setText(getString(R.string.resend_label));
            }
        }.start();
    }

    @Override
    protected void onDestroy() {
        if (resendTimer != null) resendTimer.cancel();
        super.onDestroy();
    }
}
