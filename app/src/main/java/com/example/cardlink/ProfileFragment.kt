package com.example.cardlink

import android.app.Activity
import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Matrix
import android.net.Uri
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.contract.ActivityResultContracts
import androidx.lifecycle.ViewModelProvider
import com.github.drjacky.imagepicker.ImagePicker
import com.github.drjacky.imagepicker.constant.ImageProvider
import com.google.android.material.floatingactionbutton.FloatingActionButton
import de.hdodenhof.circleimageview.CircleImageView

class ProfileFragment : Fragment() {
    private lateinit var profileImage: CircleImageView
    private lateinit var profileImageChangeButton: FloatingActionButton
    private lateinit var profileImageViewModel: ProfileImageViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_profile, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        profileImageSetUp(view)
    }

    //function handles the implementation of a users profile photo
    private fun profileImageSetUp(view:View){
        profileImage = view.findViewById(R.id.profileImage)
        profileImageChangeButton = view.findViewById(R.id.changeProfileImageButton)

        //view model observes any changes to ProfileImageViewModel value userImage (type bitmap)
        //userImage corresponds to the users profile image
        profileImageViewModel = ViewModelProvider(this)[ProfileImageViewModel::class.java]
        profileImageViewModel.userImage.observe(requireActivity()) { it ->
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



}