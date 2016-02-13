package net.devdome.bhu.app.ui.activity;

import com.google.gson.JsonObject;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatButton;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import net.devdome.bhu.app.Config;
import net.devdome.bhu.app.R;
import net.devdome.bhu.app.authentication.AccountConfig;
import net.devdome.bhu.app.utility.NetworkUtilities;

public class RegisterActivity extends AppCompatActivity implements View.OnClickListener {

    public static final String KEY_ERROR_MESSAGE = "ERR_MSG";
    TextView linkLogin;
    Button btnRegister;
    EditText etEmailAddress, etFirstName, etLastName, etMatricNo, etPassword, etConfirmPassword;
    Spinner spDepartments, spLevel;
    ProgressDialog dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        linkLogin = (TextView) findViewById(R.id.link_login);
        btnRegister = (AppCompatButton) findViewById(R.id.btn_register);
        etEmailAddress = (EditText) findViewById(R.id.et_emailAddress);
        etFirstName = (EditText) findViewById(R.id.et_firstName);
        etLastName = (EditText) findViewById(R.id.et_lastName);
        etMatricNo = (EditText) findViewById(R.id.et_matricNo);
        etPassword = (EditText) findViewById(R.id.et_password);
        etConfirmPassword = (EditText) findViewById(R.id.et_confirmPassword);
        spDepartments = (Spinner) findViewById(R.id.sp_departments);
        spLevel = (Spinner) findViewById(R.id.sp_level);

        ArrayAdapter departmentsAdapter = ArrayAdapter.createFromResource(this, R.array.departments_array, android.R.layout.simple_spinner_item);
        departmentsAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spDepartments.setAdapter(departmentsAdapter);

        ArrayAdapter levelAdapter = ArrayAdapter.createFromResource(this, R.array.level_array, android.R.layout.simple_spinner_item);
        levelAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spLevel.setAdapter(levelAdapter);

        linkLogin.setOnClickListener(this);
        btnRegister.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.link_login:
                Intent i = new Intent(this, LoginActivity.class);
                i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(i);
                break;
            case R.id.btn_register:
                String[] codes = getResources().getStringArray(R.array.department_codes_array);
                String departmentCode = codes[spDepartments.getSelectedItemPosition()];
                Log.i(Config.TAG, "Department Code: " + departmentCode);
                final String email = etEmailAddress.getText().toString();
                final String firstName = etFirstName.getText().toString();
                final String lastName = etLastName.getText().toString();
                final String matricNo = etMatricNo.getText().toString();
                final String password = etPassword.getText().toString();
                final String confirmPassword = etConfirmPassword.getText().toString();
                final String level = spLevel.getSelectedItem().toString();
                if (email.isEmpty() || firstName.isEmpty() || lastName.isEmpty() || matricNo.isEmpty() || password.isEmpty()) {
                    Toast.makeText(this, "All fields must be filled.", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (!password.equals(confirmPassword)) {
                    Toast.makeText(this, "Passwords do not match", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (!NetworkUtilities.isNetworkEnabled(this)) {
                    Toast.makeText(this, "I couldn't establish Internet connectivity", Toast.LENGTH_SHORT).show();
                    return;
                }
                new register().execute(email, firstName, lastName, matricNo, password, confirmPassword, departmentCode, level);
                break;
            default:
        }
    }

    private class register extends AsyncTask<String, Void, Intent> {
        final Intent res = new Intent();
        JsonObject loginResponse;

        @Override
        protected Intent doInBackground(String... params) {
            String emailAddress = params[0];
            String firstName = params[1];
            String lastName = params[2];
            String matricNo = params[3];
            String password = params[4];
            String confirmPassword = params[5];
            String departmentCode = params[6];
            String level = params[7];
            loginResponse = AccountConfig.serverAuthenticator.userRegistration(emailAddress, firstName, lastName, matricNo, password, confirmPassword, departmentCode, level);
            if (loginResponse.has("error")) {
                JsonObject json = loginResponse.getAsJsonObject("error").getAsJsonObject("message");
                res.putExtra(KEY_ERROR_MESSAGE, parseErrorMsg(json));
            }
            return res;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            dialog = new ProgressDialog(RegisterActivity.this, R.style.MaterialTheme_Dark_Dialog);
            dialog.setMessage("Talking to Server");
            dialog.setCancelable(false);
            dialog.show();
        }

        @Override
        protected void onPostExecute(Intent intent) {
            super.onPostExecute(intent);
            dialog.dismiss();
            if (intent.hasExtra(KEY_ERROR_MESSAGE)) {
                Toast.makeText(RegisterActivity.this, intent.getStringExtra(KEY_ERROR_MESSAGE), Toast.LENGTH_SHORT).show();
                return;
            }
            Toast.makeText(RegisterActivity.this, getResources().getString(R.string.registration_success), Toast.LENGTH_SHORT).show();
            startActivity(new Intent(RegisterActivity.this, LoginActivity.class).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));
            finish();
        }

        private String parseErrorMsg(JsonObject json) {
            if (json.has("email")) return json.getAsJsonArray("email").get(0).getAsString();
            if (json.has("password")) return json.getAsJsonArray("password").get(0).getAsString();
            if (json.has("matric_no")) return json.getAsJsonArray("matric_no").get(0).getAsString();
            return "A registration error occurred. Please contact support.";
        }
    }
}
