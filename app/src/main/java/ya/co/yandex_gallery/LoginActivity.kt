package ya.co.yandex_gallery

import android.content.Intent
import android.net.Uri
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.preference.PreferenceManager
import android.util.Log
import android.widget.Button
import android.widget.Toast
import android.widget.Toast.LENGTH_SHORT
import butterknife.BindView
import butterknife.ButterKnife
import butterknife.OnClick
import ya.co.yandex_gallery.util.AppConstants
import java.util.regex.Pattern

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
        onLogin()
    }

    private fun onLogin() {
        val uri = intent.data
        intent = null

        if (uri != null && uri.toString().startsWith(AppConstants.REDIRECT_URL)) {

            Log.d(TAG, "uri: $uri")
            val pattern = Pattern.compile("access_token=(.*?)(&|\$)")
            val matcher = pattern.matcher(uri.toString())

            if(matcher.find()) {
                val accessToken = matcher.group(1)
                Toast.makeText(this, "Welcome back, username!", LENGTH_SHORT).show()
                Log.d(TAG, "access_token: $accessToken")
                saveToken(accessToken)

            } else {
                Toast.makeText(this, "Registration token not found!", LENGTH_SHORT).show()
            }
        }
    }

    private fun saveToken(accessToken: String) {
        val editor = PreferenceManager.getDefaultSharedPreferences(this).edit()
        editor.putString(AppConstants.TOKEN_KEY, accessToken)
        editor.apply()
    }
}
