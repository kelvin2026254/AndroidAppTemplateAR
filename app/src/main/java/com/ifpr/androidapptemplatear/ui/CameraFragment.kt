package com.ifpr.androidapptemplatear.ui

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton
import com.google.ar.core.Config
import com.ifpr.androidapptemplatear.MonsterSoundManager
import com.ifpr.androidapptemplatear.R
import com.ifpr.androidapptemplatear.SharedModelViewModel
import com.ifpr.androidapptemplatear.databinding.FragmentCameraBinding
import io.github.sceneview.ar.ArSceneView
import io.github.sceneview.ar.node.ArModelNode
import io.github.sceneview.ar.node.PlacementMode
import io.github.sceneview.math.Position
import java.io.File
import java.io.IOException

class CameraFragment : Fragment() {

    private lateinit var sceneView: ArSceneView
    private lateinit var modelNode: ArModelNode
    private lateinit var placeButton: ExtendedFloatingActionButton
    private lateinit var sharedViewModel: SharedModelViewModel
    private lateinit var monsterSoundManager: MonsterSoundManager

    private val cameraPermissionCode = 1001

    private var currentModelFile = "neutrofilo.glb"
    private var _binding: FragmentCameraBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentCameraBinding.inflate(
            inflater,
            container,
            false
        )

        monsterSoundManager = MonsterSoundManager(requireContext())

        sharedViewModel = ViewModelProvider(requireActivity())
            .get(SharedModelViewModel::class.java)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        sceneView = view.findViewById<ArSceneView>(R.id.sceneView).apply {
            lightEstimationMode = Config.LightEstimationMode.DISABLED
        }

        placeButton = view.findViewById(R.id.place)

        sharedViewModel.selectedModel.observe(viewLifecycleOwner) { path ->
            if (!path.isNullOrBlank()) {
                currentModelFile = path

                if (::sceneView.isInitialized) {
                    loadModel(currentModelFile)
                }
            }
        }

        placeButton.setOnClickListener {
            if (::modelNode.isInitialized) {
                placeModel()
            } else {
                Toast.makeText(
                    requireContext(),
                    "O modelo ainda está carregando.",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }

        checkCameraPermission()
    }

    private fun placeModel() {
        if (!::modelNode.isInitialized) return

        modelNode.anchor()
        modelNode.isEditable = true
        modelNode.isScaleEditable = false

        sceneView.planeRenderer.isVisible = false

        monsterSoundManager.playMonsterSound()
    }

    private fun checkCameraPermission() {
        if (
            ContextCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.CAMERA
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                requireActivity(),
                arrayOf(Manifest.permission.CAMERA),
                cameraPermissionCode
            )
        } else {
            initAR()
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(
            requestCode,
            permissions,
            grantResults
        )

        if (requestCode == cameraPermissionCode) {
            if (
                grantResults.isNotEmpty() &&
                grantResults[0] == PackageManager.PERMISSION_GRANTED
            ) {
                initAR()
            } else {
                Toast.makeText(
                    requireContext(),
                    "Permissão de câmera negada.",
                    Toast.LENGTH_LONG
                ).show()
            }
        }
    }

    private fun initAR() {
        if (::sceneView.isInitialized) {
            loadModel(currentModelFile)
        }
    }

    private fun loadModel(fileName: String) {
        if (!::sceneView.isInitialized || _binding == null) return

        if (::modelNode.isInitialized) {
            sceneView.removeChild(modelNode)
        }

        modelNode = ArModelNode(
            sceneView.engine,
            PlacementMode.INSTANT
        ).apply {
            isEditable = true
            isScaleEditable = false

            loadModelGlbAsync(
                glbFileLocation = getFilePath(
                    requireContext(),
                    fileName
                ),
                centerOrigin = Position(0f, 0f, 1f)
            ) {
                if (_binding != null && ::sceneView.isInitialized) {
                    sceneView.planeRenderer.isVisible = true
                }
            }
        }

        sceneView.addChild(modelNode)
    }

    private fun getFilePath(
        context: Context,
        fileName: String
    ): String {
        return try {
            context.assets.open(fileName).use { }
            fileName
        } catch (e: IOException) {
            val internalFile = File(context.filesDir, fileName)

            if (internalFile.exists()) {
                Uri.fromFile(internalFile).toString()
            } else {
                fileName
            }
        }
    }

    override fun onDestroyView() {
        if (::monsterSoundManager.isInitialized) {
            monsterSoundManager.release()
        }

        _binding = null
        super.onDestroyView()
    }
}
