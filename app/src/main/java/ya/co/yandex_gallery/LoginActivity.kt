package ya.co.yandex_gallery

import android.content.Intent
import android.net.Uri
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.Toast
import android.widget.Toast.LENGTH_SHORT
import butterknife.BindView
import butterknife.ButterKnife
import butterknife.OnClick
import ya.co.yandex_gallery.util.AppConstants

class LoginActivity : AppCompatActivity() {

    val TAG = "LoginActivity"

    @BindView(R.id.button_login) lateinit var loginButton: Button

    @OnClick(R.id.button_login)
    fun loginClick() { //todo: use active dialog onCreate instead
        Log.d("Login", "clicked!")
        startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(AppConstants.AUTH_URL)))
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        ButterKnife.bind(this)
    }

    override fun onResume() {
        super.onResume()

        val uri = intent.data
        intent = null

        if (uri != null && uri.toString().startsWith(AppConstants.REDIRECT_URL)) {
//            val accessToken = uri.getEncodedSchemeSpecificPart()
//            Log.d(TAG, "access_token: $accessToken  and  ${uri.encodedPath}")
            Log.d(TAG, "uri: $uri")
            //todo: put token in preferences
//          saveToken(accessToken)

            Toast.makeText(this, "Welcome back, username!", LENGTH_SHORT).show()
        }
    }
}
