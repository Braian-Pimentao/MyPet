package com.aplicacion.mypet.activities.perfil

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.aplicacion.mypet.R
import com.aplicacion.mypet.activities.MainActivity
import com.aplicacion.mypet.providers.*
import com.google.firebase.firestore.DocumentSnapshot


class AvisoEliminar : AppCompatActivity() {
    private lateinit var mAuthProvider: AuthProvider
    private lateinit var mFavoritoProvider: FavoritoProvider
    private lateinit var mMessageProvider: MensajeProvider
    private lateinit var mImageProvider: ImageProvider
    private lateinit var mUserProvider: UserProvider
    private lateinit var mPublicacionProvider: PublicacionProvider
    private lateinit var mChatsProvider: ChatsProvider
    private lateinit var mTokenProvider: TokenProvider
    private lateinit var mReporterProvider: ReporterProvider

    private lateinit var mIdUser : String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_aviso_eliminar)

        mAuthProvider = AuthProvider()
        mFavoritoProvider = FavoritoProvider()
        mMessageProvider = MensajeProvider()
        mImageProvider = ImageProvider()
        mUserProvider = UserProvider()
        mPublicacionProvider = PublicacionProvider()
        mChatsProvider = ChatsProvider()
        mTokenProvider = TokenProvider()
        mReporterProvider = ReporterProvider()

        mIdUser = mAuthProvider.uid
    }

    fun eliminarPerfil(view: View) {
        AlertDialog.Builder(this)
                .setIcon(android.R.drawable.ic_dialog_alert)
                .setTitle(getString(R.string.eliminar_usuario))
                .setMessage(getString(R.string.comprobar_eliminar_usuario))
                .setPositiveButton(getString(R.string.si)) { dialog, which -> deleteDatosUsuario() }
                .setNegativeButton(getString(R.string.no), null).show()

    }

    private fun deleteDatosUsuario() {
        deletePublicaciones()
        deleteChats()
        deleteToken()
        deleteUsuario()

        val intent = Intent(this, MainActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
        startActivity(intent)
    }

    private fun deleteUsuario() {
        mUserProvider.getUser(mIdUser).addOnSuccessListener { documentSnapshot ->
            if (documentSnapshot.contains("urlPerfil")) {
                if (documentSnapshot["urlPerfil"] != null) {
                    mImageProvider.deleteByUrl(documentSnapshot["urlPerfil"].toString())
                }
            }
        }
        mUserProvider.deleteUser(mIdUser)

        mAuthProvider.auth.currentUser?.delete()
        mAuthProvider.auth.signOut()

    }

    private fun deleteToken() {
        mTokenProvider.deleteToken(mIdUser)
    }

    private fun deletePublicaciones() {
        mPublicacionProvider.getPostByUser(mIdUser).get().addOnSuccessListener { querySnapshot ->
            if (!querySnapshot.isEmpty) {
                val querySnapshotList: List<DocumentSnapshot> = querySnapshot.documents
                for (d in querySnapshotList) {
                    deleteFavoritos(d.id)
                    if (d.contains("imagenes")) {
                       val imagenes = d["imagenes"] as ArrayList<String>?
                        deleteImagenes(imagenes)
                    }
                    deleteReporte(d.id)
                    mPublicacionProvider.delete(d.id)
                }
            }
        }
    }

    private fun deleteReporte(idPublicacion: String) {
        mReporterProvider.getReportesByPost(idPublicacion).get().addOnSuccessListener { querySnapshot ->
            if (!querySnapshot.isEmpty) {
                val querySnapshotList: List<DocumentSnapshot> = querySnapshot.documents
                for (d in querySnapshotList) {
                    mReporterProvider.delete(d.id)
                }
            }
        }
    }

    private fun deleteImagenes(imagenes: java.util.ArrayList<String>?) {
        for (i in imagenes!!.indices) {
            mImageProvider.deleteByUrl(imagenes[i])
        }
    }

    private fun deleteChats() {
        mChatsProvider.getAll(mIdUser).get().addOnSuccessListener { querySnapshot ->
            if (!querySnapshot.isEmpty) {
                val querySnapshotList: List<DocumentSnapshot> = querySnapshot.documents
                for (d in querySnapshotList) {
                    deleteMensajes(d.id)
                    mChatsProvider.deleteChat(d.id)
                }
            }
        }
    }

    private fun deleteMensajes(idChat: String) {
        mMessageProvider.getMensajesByChat(idChat).get().addOnSuccessListener { querySnapshot ->
            if (!querySnapshot.isEmpty) {
                val querySnapshotList: List<DocumentSnapshot> = querySnapshot.documents
                for (d in querySnapshotList) {
                    mMessageProvider.deleteMensaje(d.id)
                }
            }
        }
    }

    private fun deleteFavoritos(idPublicacion: String) {
        mFavoritoProvider.deleteByPublicacion(idPublicacion).get().addOnSuccessListener { querySnapshot ->
            if (!querySnapshot.isEmpty) {
                val querySnapshotList: List<DocumentSnapshot> = querySnapshot.documents

                for (d in querySnapshotList) {
                    mFavoritoProvider.delete(d.id)
                }
            }
        }
    }

    fun cerrarEliminarPerfil(view: View) { finish()}
}