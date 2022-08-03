package com.example.cardlink.fragments

import android.app.Activity
import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Matrix
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.telephony.PhoneNumberFormattingTextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.LinearLayout
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat.recreate
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.cardlink.R
import com.example.cardlink.Util
import com.example.cardlink.interfaces.LinkContract
import com.example.cardlink.viewModels.MainViewModel
import com.example.cardlink.viewModels.ProfileViewModel
import com.firebase.ui.auth.AuthUI
import com.github.drjacky.imagepicker.ImagePicker
import com.github.drjacky.imagepicker.constant.ImageProvider
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.tabs.TabLayout
import com.google.android.material.textfield.TextInputEditText
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import de.hdodenhof.circleimageview.CircleImageView
import java.io.ByteArrayOutputStream


class ProfileFragment : Fragment(), LinkContract {
    private lateinit var profileImage: CircleImageView
    private lateinit var profileImageChangeButton: FloatingActionButton
    private lateinit var profileViewModel: MainViewModel
    private lateinit var tabLayout:TabLayout

    // Buttons
    private lateinit var logoutButton: Button
    private lateinit var saveButton: Button
    private lateinit var cancelButton: Button


    private lateinit var nameEditText: TextInputEditText
    private lateinit var descriptionEditText: TextInputEditText
    private lateinit var phoneEditText: TextInputEditText
    private lateinit var emailEditText: TextInputEditText
    private lateinit var occupationEditText: TextInputEditText

    private lateinit var name: String
    private lateinit var description: String
    private lateinit var phone: String
    private lateinit var email: String
    private lateinit var occupation: String




    private lateinit var auth: FirebaseAuth
    private lateinit var database: DatabaseReference


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
        saveButton = ret.findViewById(R.id.saveButton)
        cancelButton = ret.findViewById(R.id.cancelButton)

        nameEditText = ret.findViewById(R.id.nameTextField)
        descriptionEditText = ret.findViewById(R.id.descriptionTextField)
        emailEditText = ret.findViewById(R.id.emailTextField)
        phoneEditText = ret.findViewById(R.id.phoneTextField)

        phoneEditText.addTextChangedListener(PhoneNumberFormattingTextWatcher())
        occupationEditText = ret.findViewById(R.id.occupationTextField)

        database = Firebase.database.reference

        setUpProfileInfo2()
        logoutButton.setOnClickListener { _ ->
            signOut()
        }


        saveButton.setOnClickListener {

            profileViewModel.userImage.value?.let { it1 -> uploadImage(it1) }

            Thread(Runnable {
                val mainHandler = Handler(Looper.getMainLooper())
                if (profileViewModel.updateProfile(
                        nameEditText.text.toString(),
                        descriptionEditText.text.toString(),
                        phoneEditText.text.toString(),
                        emailEditText.text.toString(),
                        occupationEditText.text.toString()) != 0) {
                    var myRunnable = Runnable() {
                        setUpProfileInfo()
                        Toast.makeText(requireActivity(), "Error saving profile", Toast.LENGTH_SHORT).show()
                    };
                    mainHandler.post(myRunnable);
                }
                else {
                    var myRunnable = Runnable() {
                        Toast.makeText(requireActivity(), "Profile information saved successfully!", Toast.LENGTH_SHORT).show()
                    };
                    mainHandler.post(myRunnable);
                }
            }).start()
        }

        cancelButton.setOnClickListener {
            setUpProfileInfo()
            Toast.makeText(requireActivity(), "Changes discarded", Toast.LENGTH_SHORT).show()
        }


        return ret
    }

    // https://firebase.google.com/docs/storage/android/upload-files
    // This function uploads an image to Firestore. The path is "images/${userId}/profile.jpg"
    fun uploadImage(bitmap:Bitmap){
        Thread(Runnable {
            val user = auth.currentUser
            val userId = user?.uid
            val baos = ByteArrayOutputStream()
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos)
            val data: ByteArray = baos.toByteArray()
            val storage = FirebaseStorage.getInstance()
            val storageRef = storage.getReferenceFromUrl("gs://cardlink-8d22b.appspot.com")
            val imagesRef = storageRef.child("images/${userId}/profile.jpg")
            val uploadTask = imagesRef.putBytes(data)
            uploadTask.addOnFailureListener {
                println("unsuccessful upload!")
            }.addOnSuccessListener { taskSnapshot ->
                println("Successful upload!")
                // taskSnapshot.getMetadata() contains file metadata such as size, content-type, and download URL.
                // Do what you want
            }
        }).start()
    }

    fun downloadImage() {
        Thread(Runnable {
            val mainHandler = Handler(Looper.getMainLooper())
            val user = auth.currentUser
            val userId = user?.uid
            val storageReference = FirebaseStorage.getInstance().reference
            val photoReference = storageReference.child("images/${userId}/profile.jpg")
            val ONE_MEGABYTE = (1024 * 1024 * 10).toLong()

            photoReference.getBytes(ONE_MEGABYTE).addOnSuccessListener { bytes ->
                val bmp = BitmapFactory.decodeByteArray(bytes, 0, bytes.size)
                var myRunnable = Runnable() {
                    profileImage.setImageBitmap(bmp)
                };
                mainHandler.post(myRunnable);
            }.addOnFailureListener {
                Toast.makeText(
                    requireActivity(),
                    "No Such file or Path found!!",
                    Toast.LENGTH_LONG
                ).show()
            }
        }).start()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        profileImageSetUp(view)


        var layout = view.findViewById<LinearLayout>(R.id.mediaLayout)
        layout.setOnClickListener {
            val myDialog = ProfileDialogFragment()
            myDialog.setTargetFragment(this, 0)
            myDialog.show(requireActivity().supportFragmentManager, "profileFragment")
        }
    }

    private fun setUpProfileInfo2() {
        profileViewModel = ViewModelProvider(requireActivity())[MainViewModel::class.java]
        nameEditText.setText(profileViewModel.name)
        descriptionEditText.setText(profileViewModel.description)
        phoneEditText.setText(profileViewModel.phone)
        emailEditText.setText(profileViewModel.email)
        occupationEditText.setText(profileViewModel.occupation)
    }

    private fun setUpProfileInfo() {
        val user = auth.currentUser
        val userId = user?.uid
        if (userId != null) {
            downloadImage()
            // Gets all values attributed to userId
            database.child("users").child(userId).get().addOnSuccessListener {
                println("debug: entire entry ${it.value}")

                // Extract email from entry
                name = Util.asString(it.child("name").value)
                description = Util.asString(it.child("description").value)
                phone = Util.asString(it.child("phoneNumber").value)
                email = Util.asString(it.child("email").value)
                occupation = Util.asString(it.child("occupation").value)

                profileViewModel.linkedin = Util.asString(it.child("linkedin").value)
                profileViewModel.github = Util.asString(it.child("github").value)
                profileViewModel.twitter = Util.asString(it.child("twitter").value)
                profileViewModel.facebook = Util.asString(it.child("facebook").value)
                profileViewModel.website = Util.asString(it.child("website").value)

                println("linkedin from vm:  ${profileViewModel.linkedin}")

                nameEditText.setText(name)
                descriptionEditText.setText(description)
                phoneEditText.setText(phone)
                emailEditText.setText(email)
                occupationEditText.setText(occupation)
            }.addOnFailureListener{
                println("debug: firebase Error getting data $it")
            }
        }
    }

    //function handles the implementation of a users profile photo
    private fun profileImageSetUp(view:View) {
        profileImage = view.findViewById(R.id.profileImage)

        tabLayout = requireActivity().findViewById(R.id.tabLayout)

        profileImageChangeButton = view.findViewById(R.id.changeProfileImageButton)

        //view model observes any changes to ProfileImageViewModel value userImage (type bitmap)
        //userImage corresponds to the users profile image
        profileViewModel = ViewModelProvider(requireActivity())[MainViewModel::class.java]
        profileViewModel.userImage.observe(viewLifecycleOwner) { it ->
//            tabLayout.getTabAt(3)?.icon = BitmapDrawable(this.resources, it) used to replace the tab icon with image
            profileImage.setImageBitmap(it)
        }

        //based off resultCode value from activityResult we change the viewModel value userImage
        val launcher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
            if (it.resultCode == Activity.RESULT_OK) {
                val uri = it.data?.data!!
                val bitmap = getBitmap(requireActivity(), uri)
                profileViewModel.userImage.value = bitmap
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

    override fun methodToPassMyData(linkedin:String, github:String, facebook:String, twitter:String, website:String) {
        profileViewModel.linkedin = linkedin
        profileViewModel.github = github
        profileViewModel.facebook = facebook
        profileViewModel.twitter = twitter
        profileViewModel.website = website
    }

}