package jy.tool.box

import android.annotation.TargetApi
import android.app.Activity
import android.content.ContentUris
import android.content.Context
import android.content.Intent
import android.content.Intent.ACTION_GET_CONTENT
import android.database.Cursor
import android.media.ExifInterface
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.provider.DocumentsContract
import android.provider.MediaStore
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.google.gson.Gson
import jy.tool.box.databinding.ActivityExifBinding


class ExifActivity : AppCompatActivity() {
    var binding: ActivityExifBinding? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
         binding = ActivityExifBinding.inflate(layoutInflater)
        setContentView(binding!!.root)
        binding!!.imageButton.setOnClickListener {
            pickPhoto()
        }
    }

    val SELECT_PHOTO = 1
    val CROP_PHOTO = 2
    fun pickPhoto() {
        try {
            if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.KITKAT) {
                val intent = Intent(ACTION_GET_CONTENT)
                intent.setType("image/*")
                startActivityForResult(intent, SELECT_PHOTO)
            } else {
                val intent = Intent(Intent.ACTION_OPEN_DOCUMENT)
                intent.addCategory(Intent.CATEGORY_OPENABLE)
                intent.setType("image/*")
                intent.setDataAndType(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, "image/*")
                startActivityForResult(intent, SELECT_PHOTO)
            }
        } catch (e: Exception) {
            finish()
        }

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode === Activity.RESULT_OK) {
            when (requestCode) {
                SELECT_PHOTO -> {
                    val photoUri = data?.data
                    var path = getPicturePath(photoUri!!)
                    binding!!.imageView.setImageURI(photoUri)
                    var exif = ExifInterface(path!!)
                    spanExif(exif)
                    Log.e("exif", exif.toString())
                    Log.e("exif", Gson().toJson(exif))

                }


                else -> {
                }
            }
        }
    }

    private fun spanExif(exif: ExifInterface) {
        val builder = StringBuilder()
        var FFNumber = exif.getAttribute(ExifInterface.TAG_APERTURE)
        builder.append("FFNumber:${FFNumber}\n")
        var FDateTime = exif.getAttribute(ExifInterface.TAG_DATETIME)
        builder.append("FDateTime:${FDateTime}\n")
        var FExposureTime = exif.getAttribute(ExifInterface.TAG_EXPOSURE_TIME)
        builder.append("FExposureTime:${FExposureTime}\n")
        var FSubsecTime = exif.getAttribute(ExifInterface.TAG_SUBSEC_TIME)
        builder.append("FSubsecTime:${FSubsecTime}\n")
        var timeStamp = exif.getAttribute(ExifInterface.TAG_GPS_TIMESTAMP)
        builder.append("timeStamp:${timeStamp}\n")
        var timeDigitized = exif.getAttribute(ExifInterface.TAG_DATETIME_DIGITIZED)
        builder.append("timeDigitized:${timeDigitized}\n")
        var timeOriginal = exif.getAttribute(ExifInterface.TAG_DATETIME_ORIGINAL)
        builder.append("timeOriginal:${timeOriginal}\n")

        var FFlash = exif.getAttribute(ExifInterface.TAG_FLASH)
        builder.append("FFlash:${FFlash}\n")
        var flashEnergy = exif.getAttribute(ExifInterface.TAG_FLASH_ENERGY)
        builder.append("FFlash:${flashEnergy}\n")
        var FFocalLength = exif.getAttribute(ExifInterface.TAG_FOCAL_LENGTH)
        builder.append("FFocalLength:${FFocalLength}\n")
        var FImageLength = exif.getAttribute(ExifInterface.TAG_IMAGE_LENGTH)
        builder.append("FImageLength:${FImageLength}\n")
        var FImageWidth = exif.getAttribute(ExifInterface.TAG_IMAGE_WIDTH)
        builder.append("FImageWidth:${FImageWidth}\n")
        var FISOSpeedRatings = exif.getAttribute(ExifInterface.TAG_ISO)
        builder.append("FISOSpeedRatings:${FISOSpeedRatings}\n")
        var FMake = exif.getAttribute(ExifInterface.TAG_MAKE)
        builder.append("FMake:${FMake}\n")
        var FModel = exif.getAttribute(ExifInterface.TAG_MODEL)
        builder.append("FModel:${FModel}\n")
        var makerNote = exif.getAttribute(ExifInterface.TAG_MAKER_NOTE)
        builder.append("makerNote:${makerNote}\n")
        var FOrientation = exif.getAttribute(ExifInterface.TAG_ORIENTATION)
        builder.append("FOrientation:${FOrientation}\n")
        var FWhiteBalance = exif.getAttribute(ExifInterface.TAG_WHITE_BALANCE)
        builder.append("FWhiteBalance:${FWhiteBalance}\n")
        var latitude = exif.getAttribute(ExifInterface.TAG_GPS_LATITUDE)
        builder.append("latitude:${latitude}\n")
        var latitudeRef = exif.getAttribute(ExifInterface.TAG_GPS_LATITUDE_REF)
        builder.append("latitudeDef:${latitudeRef}\n")

        var longitude = exif.getAttribute(ExifInterface.TAG_GPS_LONGITUDE)
        builder.append("longitude:${longitude}\n")
        var longitudeRef = exif.getAttribute(ExifInterface.TAG_GPS_LONGITUDE_REF)
        builder.append("longitudeRef:${longitudeRef}\n")
        var alitude = exif.getAttribute(ExifInterface.TAG_GPS_ALTITUDE)
        builder.append("alitude:${alitude}\n")
        var areaInformation = exif.getAttribute(ExifInterface.TAG_GPS_AREA_INFORMATION)
        builder.append("areaInformation:${areaInformation}\n")
        binding!!.textview.text = builder
    }


    /**
     * 选择图片后，获取图片的路径
     */
    private fun getPicturePath(photoUri: Uri): String? {
        val flag = "file"
        return if (!photoUri.toString().contains(flag)) {
            getPath(this, photoUri)
        } else {
            photoUri.getPath()
        }
    }

    @TargetApi(Build.VERSION_CODES.KITKAT)
    fun getPath(context: Context, uri: Uri): String? {

        val isKitKat = Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT
        // DocumentProvider
        if (isKitKat && DocumentsContract.isDocumentUri(context, uri)) {
            // ExternalStorageProvider
            if (isExternalStorageDocument(uri)) {
                val docId = DocumentsContract.getDocumentId(uri)
                val split =
                    docId.split(":".toRegex()).dropLastWhile({ it.isEmpty() }).toTypedArray()
                val type = split[0]
                if ("primary".equals(type, ignoreCase = true)) {
                    return "${Environment.getExternalStorageDirectory()}/" + split[1]
                }
                // TODO handle non-primary volumes
            } else if (isDownloadsDocument(uri)) {
                val id = DocumentsContract.getDocumentId(uri)
                val contentUri = ContentUris.withAppendedId(
                    Uri.parse("content://downloads/public_downloads"), java.lang.Long.valueOf(id)
                )
                return getDataColumn(context, contentUri, null, null)
            } else if (isMediaDocument(uri)) {
                val docId = DocumentsContract.getDocumentId(uri)
                val split =
                    docId.split(":".toRegex()).dropLastWhile({ it.isEmpty() }).toTypedArray()
                val type = split[0]

                var contentUri: Uri? = null
                if ("image" == type) {
                    contentUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI
                } else if ("video" == type) {
                    contentUri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI
                } else if ("audio" == type) {
                    contentUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI
                }

                val selection = "_id=?"
                val selectionArgs = arrayOf<String>(split[1])

                return getDataColumn(context, contentUri, selection, selectionArgs)
            }// MediaProvider
            // DownloadsProvider
        } else if ("content".equals(uri.scheme!!, ignoreCase = true)) {
            return if (isGooglePhotosUri(uri)) {
                uri.lastPathSegment
            } else getDataColumn(context, uri, null, null)
        } else if ("file".equals(uri.scheme!!, ignoreCase = true)) {
            return uri.path
        }

        return null
    }

    fun isExternalStorageDocument(uri: Uri): Boolean {
        return "com.android.externalstorage.documents" == uri.authority
    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is DownloadsProvider.
     */
    fun isDownloadsDocument(uri: Uri): Boolean {
        return "com.android.providers.downloads.documents" == uri.authority
    }

    fun isGooglePhotosUri(uri: Uri): Boolean {
        return "com.google.android.apps.photos.content" == uri.authority
    }

    fun getDataColumn(
        context: Context, uri: Uri?, selection: String?,
        selectionArgs: Array<String>?
    ): String? {

        var cursor: Cursor? = null
        val column = "_data"
        val projection = arrayOf(column)

        try {
            cursor =
                context.contentResolver.query(uri!!, projection, selection, selectionArgs, null)
            if (cursor != null && cursor!!.moveToFirst()) {
                val index = cursor!!.getColumnIndexOrThrow(column)
                return cursor!!.getString(index)
            }
        } finally {
            if (cursor != null) {
                cursor!!.close()
            }
        }
        return null
    }

    fun isMediaDocument(uri: Uri): Boolean {
        return "com.android.providers.media.documents" == uri.authority
    }


}
