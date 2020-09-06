package io.keepcoding.eh_ho.login

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import io.keepcoding.eh_ho.R
import io.keepcoding.eh_ho.data.SignUpModel
import io.keepcoding.eh_ho.inflate
import kotlinx.android.synthetic.main.fragment_create_post.*
import kotlinx.android.synthetic.main.fragment_sign_in.*
import kotlinx.android.synthetic.main.fragment_sign_up.*
import kotlinx.android.synthetic.main.fragment_sign_up.inputPassword
import kotlinx.android.synthetic.main.fragment_sign_up.inputUsername
import kotlinx.android.synthetic.main.fragment_sign_up.labelCreateAccount

class SignUpFragment : Fragment() {

    var signUpInteractionListener: SignUpInteractionListener?
        = null

    override fun onAttach(context: Context) {
        super.onAttach(context)

        if (context is SignUpInteractionListener)
            signUpInteractionListener = context
        else
            throw IllegalArgumentException("Context doesn't implement ${SignUpInteractionListener::class.java.canonicalName}")
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return container?.inflate(R.layout.fragment_sign_up)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        buttonSignUp.setOnClickListener {
            // Check empties
            if (this.isFormValid() &&
                this.checkPass() &&
                this.validateEmail()) {
                val model = SignUpModel(
                    inputUsername.text.toString(),
                    inputEmail.text.toString(),
                    inputPassword.text.toString()
                )

                signUpInteractionListener?.onSignUp(model)
            } else {
                this.showErrorRequired()
            }
        }

        labelCreateAccount.setOnClickListener {
            signUpInteractionListener?.onGoToSignIn()
        }
    }

    private fun showErrorRequired() {
        if (inputUsername.text.isEmpty())
            inputUsername.error = getString(R.string.error_empty)
        if (inputEmail.text.isEmpty())
            inputEmail.error = getString(R.string.error_empty)
        if (inputPassword.text.isEmpty())
            inputPassword.error = getString(R.string.error_empty)
        if (inputConfirmPassword.text.isEmpty())
            inputConfirmPassword.error = getString(R.string.error_empty)
        if (!this.checkPass())
            inputPassword.error = getString(R.string.error_confirm_pass)
        if (!validateEmail())
            inputEmail.error = getString(R.string.error_email)
    }


    override fun onDetach() {
        super.onDetach()
        this.signUpInteractionListener = null
    }

    interface SignUpInteractionListener {
        fun onGoToSignIn()
        fun onSignUp(signUpModel: SignUpModel)
    }

    // Validations

    private fun validateEmail() = android.util.Patterns.EMAIL_ADDRESS.matcher(inputEmail.text).matches()

    private fun checkPass() = inputPassword.text.toString() == inputConfirmPassword.text.toString()

    private fun isFormValid() = inputUsername.text.isNotEmpty() &&
                                inputEmail.text.isNotEmpty() &&
                                inputPassword.text.isNotEmpty() &&
                                inputConfirmPassword.text.isNotEmpty()

}