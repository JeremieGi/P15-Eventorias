package com.openclassrooms.p15_eventorias

import android.content.Context
import com.openclassrooms.p15_eventorias.utils.createImageFile
import com.openclassrooms.p15_eventorias.utils.getPathCacheImgPhoto
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Before
import org.junit.Test
import org.mockito.ArgumentMatchers.anyString
import org.mockito.ArgumentMatchers.eq
import org.mockito.Mockito.mock
import org.mockito.Mockito.verify
import org.mockito.Mockito.`when`
import org.mockito.MockitoAnnotations
import java.io.File

// TODO Denis : d'autres tests unitaires ?

class UtilsFileTest {

    private lateinit var mockedContext: Context
    private lateinit var mockedPhotoDir: File

    @Before
    fun setup() {
        MockitoAnnotations.openMocks(this)

        // Simuler un contexte Android
        mockedContext = mock(Context::class.java)

        // Simuler le répertoire de cache d'images
        mockedPhotoDir = mock(File::class.java)
        `when`(mockedContext.getPathCacheImgPhoto()).thenReturn(mockedPhotoDir)

        // Simuler l'existence et la création des répertoires
        `when`(mockedPhotoDir.exists()).thenReturn(false) // Le répertoire n'existe pas initialement
        `when`(mockedPhotoDir.mkdirs()).thenReturn(true)  // Le répertoire est créé avec succès
    }

    @Test
    fun testCreateImageFile_createsFileSuccessfully() {

        // Simuler la création réussie du fichier temporaire
        val mockFile = mock(File::class.java)
        `when`(
            File.createTempFile(anyString(), eq(".jpg"), eq(mockedPhotoDir)) // TODO Denis : Test qui passe pas : Prefix string "" too short: length must be at least 3
            //File.createTempFile(argThat { it != null && it.length >= 3 }, eq(".jpg"), eq(mockedPhotoDir))
        ).thenReturn(mockFile)

        // fonction à tester
        val createdFile = mockedContext.createImageFile()

        // Vérifier que le fichier est bien créé
        assertNotNull(createdFile)
        assertEquals(mockFile, createdFile)
        verify(mockedPhotoDir).mkdirs() // Vérifie que mkdirs() a bien été appelé
    }

}