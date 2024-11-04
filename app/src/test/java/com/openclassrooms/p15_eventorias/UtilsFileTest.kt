package com.openclassrooms.p15_eventorias

import android.content.Context
import com.openclassrooms.p15_eventorias.utils.createImageFile
import io.mockk.every
import io.mockk.mockk
import io.mockk.mockkStatic
import io.mockk.unmockkStatic
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.mockito.Mockito.mock
import io.mockk.verify
import java.io.File


class UtilsFileTest {

    private lateinit var mockedContext: Context

    @Before
    fun setUp() {
        // Moquer la méthode statique :  File.createTempFile
        mockkStatic(File::class)

        // Simuler un contexte Android
        mockedContext = mock(Context::class.java)
    }

    @After
    fun tearDown() {
        // Démoquer la méthode statique
        unmockkStatic(File::class)
    }

    @Test
    fun testCreateImageFile_createsFileSuccessfully() {

        // Simuler la création réussie du fichier temporaire
        val mockFile = mockk<File>(relaxed = true) // L’option relaxed = true est utilisée pour éviter de devoir simuler toutes les méthodes non utilisées.
        every {
            File.createTempFile(any(), any(), any())
        } returns mockFile


        // fonction à tester
        val createdFile = mockedContext.createImageFile()

        // Vérifier que la méthode a été appelée avec les bons arguments
        verify { //=> Echoue !!! Argument passed to verify() is of type File and is not a mock!
            File.createTempFile(any(), any(), any())
        }

        // Vérifiez que le fichier retourné est le bon
        assert(createdFile == mockFile)

    }

}