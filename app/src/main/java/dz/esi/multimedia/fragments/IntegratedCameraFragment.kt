package dz.esi.multimedia.fragments


import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.SurfaceTexture
import android.hardware.camera2.*
import android.os.Bundle
import android.os.Environment
import android.os.Handler
import android.os.HandlerThread
import android.util.Log
import android.util.Size
import android.view.LayoutInflater
import android.view.Surface
import android.view.TextureView.SurfaceTextureListener
import android.view.View
import android.view.ViewGroup
import androidx.core.app.ActivityCompat
import androidx.fragment.app.Fragment
import dz.esi.multimedia.R
import kotlinx.android.synthetic.main.fragment_integrated_camera.*
import kotlinx.android.synthetic.main.fragment_integrated_camera.view.*
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*


/**
 * A simple [Fragment] sass.
 */
class IntegratedCameraFragment : Fragment() {

    // private var mCamera: Camera? = null
    // private var mPreview: CameraPreview? = null
    lateinit var cameraManager: CameraManager

    var cameraFacing: Int = 0

    lateinit var surfaceTextureListener: SurfaceTextureListener

    lateinit var previewSize: Size

    var cameraId: String? = null

    var backgroundThread: HandlerThread? = null

    var backgroundHandler: Handler? = null

    var cam: CameraDevice? = null

    var session: CameraCaptureSession? = null

    lateinit var galleryFolder: File


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        val rootView = inflater.inflate(R.layout.fragment_integrated_camera, container, false)
        createImageGallery()
        val btn = rootView.btnPhoto
        btn.setOnClickListener({
           takePhoto()
        });
        return rootView
    }

    private fun prendrePhoto() {
        // mCamera?.takePicture(null, null, this)
    }

    override fun onAttach(context: Context?) {
        super.onAttach(context)
        if (context != null) {
            cameraManager = context.getSystemService(Context.CAMERA_SERVICE) as CameraManager
            cameraFacing = CameraCharacteristics.LENS_FACING_BACK
            surfaceTextureListener = object : SurfaceTextureListener {
                override fun onSurfaceTextureAvailable(surfaceTexture: SurfaceTexture, width: Int, height: Int) {
                    setUpCamera()
                    openCamera()
                }

                override fun onSurfaceTextureSizeChanged(surfaceTexture: SurfaceTexture, width: Int, height: Int) {}
                override fun onSurfaceTextureDestroyed(surfaceTexture: SurfaceTexture): Boolean {
                    return false
                }

                override fun onSurfaceTextureUpdated(surfaceTexture: SurfaceTexture) {}
            }
        }
    }


    private fun setUpCamera() {
        try {
            for (cameraId in cameraManager.cameraIdList) {
                val cameraCharacteristics = cameraManager.getCameraCharacteristics(cameraId)
                if (cameraCharacteristics.get(CameraCharacteristics.LENS_FACING) ===
                        cameraFacing) {
                    val streamConfigurationMap = cameraCharacteristics.get(
                            CameraCharacteristics.SCALER_STREAM_CONFIGURATION_MAP)
                    previewSize = streamConfigurationMap!!.getOutputSizes(SurfaceTexture::class.java)[0]
                    this.cameraId = cameraId
                }
            }
        } catch (e: CameraAccessException) {
            e.printStackTrace()
        }
    }

    private fun openCamera() {
        try {
            val stateCallback = object : CameraDevice.StateCallback() {
                override fun onOpened(cameraDevice: CameraDevice) {
                    cam = cameraDevice
                    createPreviewSession()
                }

                override fun onDisconnected(cameraDevice: CameraDevice) {
                    cameraDevice.close()
                    cam = null
                }

                override fun onError(cameraDevice: CameraDevice, error: Int) {
                    cameraDevice.close()
                    cam = null
                }
            }
            if (this.activity?.baseContext?.let { ActivityCompat.checkSelfPermission(it, Manifest.permission.CAMERA) } != PackageManager.PERMISSION_GRANTED) {
                // TODO: Consider calling
                //    ActivityCompat#requestPermissions
                // here to request the missing permissions, and then overriding
                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                //                                          int[] grantResults)
                // to handle the case where the user grants the permission. See the documentation
                // for ActivityCompat#requestPermissions for more details.
                return
            }
            cameraManager.openCamera(cameraId!!, stateCallback, backgroundHandler!!)
        } catch (e: CameraAccessException) {
            e.printStackTrace()
        }
    }

    private fun openBackgroundThread() {
        backgroundThread = HandlerThread("camera_background_thread")
        backgroundThread?.start()
        backgroundHandler = Handler(backgroundThread?.getLooper())
    }

    override fun onResume() {
        super.onResume()
        super.onResume()
        openBackgroundThread()
        if (texture_view.isAvailable()) {
            setUpCamera();
            openCamera();
        } else {
            texture_view.setSurfaceTextureListener(surfaceTextureListener);
        }
    }

    override fun onStop() {
        super.onStop()
        closeCamera()
        closeBackgroundThread()
    }

    private fun closeCamera() {
        if (session != null) {
            session?.close()
            session = null
        }
        if (cam != null) {
            cam?.close()
            cam = null
        }
    }

    private fun closeBackgroundThread() {
        if (backgroundHandler != null) {
            backgroundThread?.quitSafely()
            backgroundThread = null
            backgroundHandler = null
        }
    }

    private fun createPreviewSession() {
        try {
            val surfaceTexture: SurfaceTexture = texture_view.getSurfaceTexture()
            surfaceTexture.setDefaultBufferSize(previewSize.width, previewSize.height)
            val previewSurface = Surface(surfaceTexture)
            val captureRequestBuilder = cam?.createCaptureRequest(CameraDevice.TEMPLATE_PREVIEW)
            captureRequestBuilder?.addTarget(previewSurface)

            cam?.createCaptureSession(mutableListOf(previewSurface),
                    object : CameraCaptureSession.StateCallback() {
                        override fun onConfigured(cameraCaptureSession: CameraCaptureSession) {
                            if (cam == null) {
                                return
                            }
                            try {
                                val captureRequest = captureRequestBuilder?.build()
                                session = cameraCaptureSession
                                session?.setRepeatingRequest(captureRequest!!,
                                        null, backgroundHandler)
                            } catch (e: CameraAccessException) {
                                e.printStackTrace()
                            }
                        }

                        override fun onConfigureFailed(cameraCaptureSession: CameraCaptureSession) {}
                    }, backgroundHandler)
        } catch (e: CameraAccessException) {
            e.printStackTrace()
        }
    }

    private fun createImageGallery() {
        val storageDirectory: File = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES)
        galleryFolder = File(storageDirectory, resources.getString(R.string.app_name))
        if (!galleryFolder.exists()) {
            val wasCreated: Boolean = galleryFolder.mkdirs()
            if (!wasCreated) {
                Log.e("CapturedImages", "Failed to create directory")
            }
        }
    }

    private fun createImageFile(galleryFolder: File): File? {
        val timeStamp: String = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())
        val imageFileName = "image_" + timeStamp + "_"
        return File.createTempFile(imageFileName, ".jpg", galleryFolder)
    }

    fun takePhoto() {
        var outputPhoto: FileOutputStream? = null
        try {
            outputPhoto = FileOutputStream(createImageFile(galleryFolder))
            texture_view.getBitmap()
                    .compress(Bitmap.CompressFormat.PNG, 100, outputPhoto)
        } catch (e: Exception) {
            e.printStackTrace()
        } finally {
            try {
                if (outputPhoto != null) {
                    outputPhoto.close()
                }
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }
    }


}// Required empty public constructor
