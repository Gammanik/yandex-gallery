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
import com.gigamole.library.PulseView
import ya.co.yandex_gallery.util.AppConstants
import java.util.regex.Pattern

class LoginActivity : AppCompatActivity() {

    val TAG = "LoginActivity"

    @BindView(R.id.yandex_login_image) lateinit var loginImagePulseView: PulseView
    @BindView(R.id.button_without_reg) lateinit var button: Button

    @OnClick(R.id.yandex_login_image)
    fun loginClick() {
        startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(AppConstants.AUTH_URL)))
    }

    @OnClick(R.id.button_without_reg)
    fun anonymousClick() {
        val intent = Intent(this, FeedActivity::class.java)
        intent.putExtra(AppConstants.KEY_IS_CONTINUE_ANON, true)
        startActivity(intent)
//            startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(AppConstants.AUTH_URL)))
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        ButterKnife.bind(this)

        loginImagePulseView.startPulse()
    }

    override fun onResume() {
        super.onResume()
        onLogin()
    }

    private fun onLogin() {
        val uri = intent?.data
        intent = null

        if (uri != null && uri.toString().startsWith(AppConstants.REDIRECT_URL)) {

            loginImagePulseView.finishPulse()
            val pattern = Pattern.compile("access_token=(.*?)(&|\$)")
            val matcher = pattern.matcher(uri.toString())

            if(matcher.find()) {
                val accessToken = matcher.group(1)
                Toast.makeText(this, "Welcome back, username!", LENGTH_SHORT).show()
                saveToken(accessToken)

                val intent = Intent(this, FeedActivity::class.java)
                this.startActivity(intent)

            } else {
                Toast.makeText(this, "Registration token not found!", LENGTH_SHORT).show()
            }
        }
    }

    private fun saveToken(accessToken: String) {
        val editor = PreferenceManager.getDefaultSharedPreferences(this).edit()
        editor.putString(AppConstants.KEY_TOKEN, accessToken)
        editor.apply()
    }
}
