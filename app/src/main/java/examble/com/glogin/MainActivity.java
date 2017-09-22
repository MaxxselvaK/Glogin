package examble.com.glogin;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutCompat;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInApi;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;

import org.w3c.dom.Text;

public class MainActivity extends AppCompatActivity implements View.OnClickListener,GoogleApiClient.OnConnectionFailedListener {
    private LinearLayout prof_section;
    private Button SignOut;
    private SignInButton SignIn;
    private TextView name,email;
    private ImageView profile;
    private GoogleApiClient gApiClt;
    private static final int REQ_CODE = 9001;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        prof_section = (LinearLayout)findViewById(R.id.prof_section);
        name = (TextView)findViewById(R.id.tv_name);
        email = (TextView)findViewById(R.id.tv_mail);
        SignOut = (Button)findViewById(R.id.signOut);
        SignIn = (SignInButton)findViewById(R.id.signIn);
        profile = (ImageView)findViewById(R.id.prof);
        prof_section.setVisibility(View.GONE);
        SignIn.setOnClickListener(this);
        SignOut.setOnClickListener(this);
        GoogleSignInOptions signInOptions = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN).requestEmail().build();
        gApiClt = new GoogleApiClient.Builder(this).enableAutoManage(this,this).addApi(Auth.GOOGLE_SIGN_IN_API,signInOptions).build();
    }

    @Override
    public void onClick(View v) {
    switch(v.getId()){
        case R.id.signIn:
            signIn();
            break;
        case R.id.signOut:
            signOut();
            break;
    }
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }
    public void signIn(){

        Intent intent = Auth.GoogleSignInApi.getSignInIntent(gApiClt);
        startActivityForResult(intent,REQ_CODE);
    }
    public void signOut(){
        Auth.GoogleSignInApi.signOut(gApiClt).setResultCallback(new ResultCallback<Status>() {
            @Override
            public void onResult(@NonNull Status status) {
                updateUI(false);
            }
        });
    }
    public void handleResult(GoogleSignInResult data){
        if(data.isSuccess()){
            GoogleSignInAccount account = data.getSignInAccount();
            String acc_name = account.getDisplayName();
            String acc_email = account.getEmail();
            String imageUrl = account.getPhotoUrl().toString();
            name.setText(acc_name);
            email.setText(acc_email);
            Glide.with(this).load(imageUrl).into(profile);
            updateUI(true);
        }
        else{
            updateUI(false);
        }
    }
    public void updateUI(boolean isLogin){
        if(isLogin){
            prof_section.setVisibility(View.VISIBLE);
            SignIn.setVisibility(View.GONE);
        }
        else
        {
            prof_section.setVisibility(View.GONE);
            SignIn.setVisibility(View.VISIBLE);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == REQ_CODE){
            GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
            handleResult(result);
        }
    }
}
