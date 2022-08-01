package com.example.cardlink.fragments

import android.app.Activity
import android.content.ContentValues.TAG
import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Matrix
import android.graphics.drawable.BitmapDrawable
import android.net.Uri
import android.os.Bundle
import android.text.Editable
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat.recreate
import androidx.core.content.res.ResourcesCompat
import androidx.lifecycle.ViewModelProvider
import com.example.cardlink.viewModels.ProfileImageViewModel
import com.example.cardlink.R
import com.firebase.ui.auth.AuthUI
import com.github.drjacky.imagepicker.ImagePicker
import com.github.drjacky.imagepicker.constant.ImageProvider
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.tabs.TabItem
import com.google.android.material.tabs.TabLayout
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import de.hdodenhof.circleimageview.CircleImageView

class ProfileFragment : Fragment() {
    private lateinit var profileImage: CircleImageView
    private lateinit var profileImageChangeButton: FloatingActionButton
    private lateinit var profileImageViewModel: ProfileImageViewModel
    private lateinit var tabLayout:TabLayout
    private lateinit var logoutButton: Button
    private lateinit var emailTextView: TextInputEditText
    private lateinit var auth: FirebaseAuth
    private lateinit var database: DatabaseReference
    private lateinit var email: String

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        // Inflate the layout for this fragment
        val ret = inflater.inflate(R.layout.fragment_profile, container, false)

        // Get database ref
        database = Firebase.database.reference
        auth = Firebase.auth

        logoutButton = ret.findViewById(R.id.logout_button)
        emailTextView = ret.findViewById(R.id.emailTextField)

        val user = auth.currentUser
        val userId = user?.uid
        database = Firebase.database.reference

        if (userId != null) {

            // Gets all values attributed to userId
            database.child("users").child(userId).get().addOnSuccessListener {
                println("debug: entire entry ${it.value}")

                // Extract email from entry
                email = ""
                val _email = it.child("email").value
                if(_email != null)
                    email = _email as String
                emailTextView.setText(email)

            }.addOnFailureListener{
                println("debug: firebase Error getting data $it")
            }
        }

        logoutButton.setOnClickListener { _ ->
            signOut()
        }

        return ret
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        profileImageSetUp(view)

        var layout = view.findViewById<LinearLayout>(R.id.mediaLayout)
        layout.setOnClickListener {
            val myDialog = ProfileDialogFragment()
            myDialog.show(requireActivity().supportFragmentManager, "profileFragment")
        }
    }

    //function handles the implementation of a users profile photo
    private fun profileImageSetUp(view:View){
        profileImage = view.findViewById(R.id.profileImage)

        tabLayout = requireActivity().findViewById(R.id.tabLayout)

        profileImageChangeButton = view.findViewById(R.id.changeProfileImageButton)

        //view model observes any changes to ProfileImageViewModel value userImage (type bitmap)
        //userImage corresponds to the users profile image
        profileImageViewModel = ViewModelProvider(this)[ProfileImageViewModel::class.java]
        profileImageViewModel.userImage.observe(viewLifecycleOwner) { it ->
//            tabLayout.getTabAt(3)?.icon = BitmapDrawable(this.resources, it) used to replace the tab icon with image
            profileImage.setImageBitmap(it)
        }

        //based off resultCode value from activityResult we change the viewModel value userImage
        val launcher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
            if (it.resultCode == Activity.RESULT_OK) {
                val uri = it.data?.data!!
                val bitmap = getBitmap(requireActivity(), uri)
                profileImageViewModel.userImage.value = bitmap
            }
        }

        //Dialog for choosing between taking a camera photo or choosing from photo gallery
        profileImageChangeButton.setOnClickListener{
            ImagePicker.with(requireActivity())
                .provider(ImageProvider.BOTH) //Or bothCameraGallery()
                .createIntentFromDialog { launcher.launch(it) }
        }
    }




    //helper function for converting uri to bitmap
    private fun getBitmap(context: Context, imgUri: Uri): Bitmap {
        var bitmap = BitmapFactory.decodeStream(context.contentResolver.openInputStream(imgUri))
        val matrix = Matrix()
        matrix.setRotate(0f)
        return Bitmap.createBitmap(bitmap, 0, 0, bitmap.width, bitmap.height, matrix, true)
    }

    // Reference: https://firebase.google.com/docs/auth/android/firebaseui#sign_in
    private fun signOut() {

        AuthUI.getInstance()
            .signOut(requireActivity())
            .addOnCompleteListener {
                // Restart app -> redirected to login page
                recreate(requireActivity())
            }
    }


}