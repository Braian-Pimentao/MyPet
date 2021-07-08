package com.aplicacion.mypet.activities.chat

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.widget.*
import androidx.appcompat.app.ActionBar
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.AdapterDataObserver
import com.aplicacion.mypet.R
import com.aplicacion.mypet.activities.perfil.ActivityUsuario
import com.aplicacion.mypet.adaptadores.MessageAdapter
import com.aplicacion.mypet.models.Chat
import com.aplicacion.mypet.models.FCMBody
import com.aplicacion.mypet.models.FCMResponse
import com.aplicacion.mypet.models.Mensaje
import com.aplicacion.mypet.providers.*
import com.aplicacion.mypet.utils.AppInfo
import com.aplicacion.mypet.utils.RelativeTime
import com.aplicacion.mypet.utils.ViewedMessageHelper
import com.firebase.ui.firestore.FirestoreRecyclerOptions
import com.google.firebase.firestore.ListenerRegistration
import com.google.gson.Gson
import com.squareup.picasso.Picasso
import de.hdodenhof.circleimageview.CircleImageView
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.*
import kotlin.collections.ArrayList

class ActivityChat : AppCompatActivity() {
    private lateinit var extraIdUser1: String
    private lateinit var extraIdUser2: String
    private var extraIdChat: String? = null

    private lateinit var chatsProvider: ChatsProvider
    private lateinit var mensajeProvider: MensajeProvider
    private lateinit var authProvider: AuthProvider
    private lateinit var userProvider: UserProvider
    private lateinit var notificationProvider: NotificationProvider
    private lateinit var tokenProvider: TokenProvider

    private lateinit var imagenUsuario: CircleImageView
    private lateinit var nombreUsuario: TextView
    private lateinit var infoChat: TextView


    private lateinit var editTextMensaje: EditText
    private lateinit var recyclerViewMensajes: RecyclerView
    private lateinit var linearLayoutManager: LinearLayoutManager


    private var mAdapter: MessageAdapter? = null

    private lateinit var miNombreDeUsuario: String
    private lateinit var nombreDeUsuarioChat: String
    private lateinit var imagenSender: String
    private lateinit var linearLayoutInformativo: LinearLayout


    private lateinit var actionBarView: View
    private var idNotificationChat: Long = 0
    private var register: Register? = null


    private lateinit var mListener: ListenerRegistration

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chat)

        chatsProvider = ChatsProvider()
        mensajeProvider = MensajeProvider()
        authProvider = AuthProvider()
        userProvider = UserProvider()
        notificationProvider = NotificationProvider()
        tokenProvider = TokenProvider()

        extraIdUser1 = intent.getStringExtra("idUser1")!!
        extraIdUser2 = intent.getStringExtra("idUser2")!!
        extraIdChat = intent.getStringExtra("idChat")

        editTextMensaje = findViewById(R.id.edit_text_mensaje)

        recyclerViewMensajes = findViewById(R.id.recyclerViewMensajes)
        linearLayoutManager = LinearLayoutManager(this@ActivityChat)
        linearLayoutManager.stackFromEnd = true
        recyclerViewMensajes.layoutManager = linearLayoutManager
        register = Register()

        linearLayoutInformativo = findViewById(R.id.mensaje_informativo_chat)


        showCustomToolbar(R.layout.custom_chat_toolbar)
        getMyInfoUser()

        AppInfo.init(true)
        checkifChatExist()
    }

    private fun showCustomToolbar(resource: Int) {
        val toolbar: Toolbar = findViewById(R.id.toolBar_chat)
        setSupportActionBar(toolbar)
        val actionBar: ActionBar? = supportActionBar
        actionBar?.title = ""
        actionBar?.setDisplayShowCustomEnabled(true)
        val inflater = this.getSystemService(LAYOUT_INFLATER_SERVICE) as LayoutInflater
        actionBarView = inflater.inflate(resource, null)
        actionBar?.customView = actionBarView

        imagenUsuario = actionBarView.findViewById(R.id.imagen_perfil_chat)
        nombreUsuario = actionBarView.findViewById(R.id.user_chat_toolbar)
        infoChat = actionBarView.findViewById(R.id.info_user_toolbar)

        val layout = actionBarView.findViewById<RelativeLayout>(R.id.mostrarPerfilUsuarioChat)
        layout.setOnClickListener {
            val idUser = if (authProvider.uid == extraIdUser1)
                extraIdUser2
            else
                extraIdUser1

            val userIntent = Intent(this, ActivityUsuario::class.java)
            userIntent.putExtra("idUser", idUser)
            startActivity(userIntent)
        }

        getUserInfo()
    }

    private fun getUserInfo() {
        val idUserInfo = if (authProvider.uid == extraIdUser1) {
            extraIdUser2
        } else {
            extraIdUser1
        }

        mListener = userProvider.getUserRealTime(idUserInfo).addSnapshotListener { value, error ->
            if (value!!.exists()) {
                if (value.contains("username")) {
                    nombreDeUsuarioChat = value.getString("username").toString()
                    nombreUsuario.text = nombreDeUsuarioChat
                }

                if (value.contains("online")) {
                    val online = value.getBoolean("online")!!
                    if (online) {
                        infoChat.text = getString(R.string.online)
                    } else if (value.contains("ultimaConexion")) {
                        infoChat.text = RelativeTime.timeFormatAMPM(value.getLong("ultimaConexion")!!, this@ActivityChat)
                    }
                }

                if (value.contains("urlPerfil")) {
                    val imageProfile = value.getString("urlPerfil")
                    if (imageProfile != null)
                        if (imageProfile.isNotEmpty())
                            Picasso.get().load(imageProfile).into(imagenUsuario)
                }
            }
        }
    }

    private fun checkifChatExist() {
        chatsProvider.getChatByUser1AndUser2(extraIdUser1, extraIdUser2).get().addOnSuccessListener { querySnapshot ->
            val size = querySnapshot.size()
            if (size == 0) {
                linearLayoutInformativo.visibility = View.VISIBLE
                createChat()
            } else {
                linearLayoutInformativo.visibility = View.GONE
                extraIdChat = querySnapshot.documents[0].id
                idNotificationChat = querySnapshot.documents[0].getLong("idNotificacion")!!
                getMensajesChat()
                updateViewed()
            }
        }
    }

    override fun onResume() {
        super.onResume()
        checkifChatExist()
        register = Register()
    }

    private fun updateViewed() {
        val idSender = if (authProvider.uid == extraIdUser1)
            extraIdUser2
        else
            extraIdUser1

        mensajeProvider.getMensajeByChatAndSender(extraIdChat, idSender).get().addOnSuccessListener { querySnapshot ->
            for ( document in querySnapshot.documents) {
                mensajeProvider.updateVisto(document.id, true)
            }
        }
    }

    override fun onStart() {
        super.onStart()
        if (mAdapter != null) {
            mAdapter?.startListening()
        }
        ViewedMessageHelper.updateOnline(true, this@ActivityChat)
    }

    private fun getMensajesChat() {
        var query = mensajeProvider.getMensajesByChat(extraIdChat)
        var options = FirestoreRecyclerOptions.Builder<Mensaje>()
                .setQuery(query, Mensaje::class.java)
                .build()

        mAdapter = MessageAdapter(options, this)
        recyclerViewMensajes.adapter = mAdapter
        mAdapter?.startListening()
        mAdapter?.registerAdapterDataObserver(register!!)
    }

    override fun onStop() {
        super.onStop()
        register = null
        intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
        mAdapter?.stopListening()
        comprobarMensajes()
        AppInfo.init(false)
    }

    private fun createChat() {
        val chat = Chat()
        chat.idUser1 = extraIdUser1
        chat.idUser2 = extraIdUser2
        chat.timestamp = Date().time
        chat.id = extraIdUser1+extraIdUser2
        val ids = ArrayList<String>()
        ids.add(extraIdUser1)
        ids.add(extraIdUser2)
        chat.ids = ids
        var random = Random()
        idNotificationChat = random.nextInt(1000000).toLong()
        chat.idNotificacion = idNotificationChat.toInt()
        chatsProvider.create(chat)
        extraIdChat = chat.id
        getMensajesChat()
    }

    fun cerrarChat(view: View?) {
        comprobarMensajes()
        finish()
    }

    override fun onDestroy() {
        super.onDestroy()
        if (mListener != null) {
            mListener.remove()
        }
    }

    private fun comprobarMensajes() {
        mensajeProvider.getMensajesByChat(extraIdChat).get().addOnSuccessListener { querySnapshot ->
            if (querySnapshot.size() == 0)
                chatsProvider.deleteChat(extraIdChat)
        }
    }

    override fun onBackPressed() {
        super.onBackPressed()
        finish()
    }

    fun enviarMensaje(view: View?) {
        var textoMensaje = editTextMensaje.text.toString().trim()
        if (textoMensaje.isNotEmpty()) {
            linearLayoutInformativo.visibility = View.GONE
            var mensaje = Mensaje()
            if (authProvider.uid == extraIdUser1) {
                mensaje.idSender = extraIdUser1
                mensaje.idReceiver = extraIdUser2
            } else {
                mensaje.idSender = extraIdUser2
                mensaje.idReceiver = extraIdUser1
            }
            mensaje.idChat = extraIdChat
            mensaje.mensaje = textoMensaje
            mensaje.timestamp = Date().time
            mensaje.isVisto = false

            mensajeProvider.create(mensaje).addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    editTextMensaje.setText("")
                    mAdapter?.notifyDataSetChanged()
                    getToken(mensaje)
                }
            }
        }
    }

    private fun getToken(mensaje: Mensaje) {
        val idUser = if (authProvider.uid == extraIdUser1) {
            extraIdUser2
        } else {
            extraIdUser1
        }

        tokenProvider.getToken(idUser)!!.addOnSuccessListener { documentSnapshot ->
            if (documentSnapshot!!.exists()) {
                if (documentSnapshot.contains("token")) {
                    val token = documentSnapshot.getString("token")
                    getTresUltimosMensajes(mensaje, token!!)
                }
            } else {
                Toast.makeText(this@ActivityChat, "El token de notificaciones del usuario no existe", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun getTresUltimosMensajes(mensaje: Mensaje, token: String) {
        mensajeProvider.getTresUltimosMensajesByChatAndSender(extraIdChat, authProvider.uid).get().addOnSuccessListener { queryDocumentSnapshots ->
            val mensajeArrayList = java.util.ArrayList<Mensaje?>()
            for (d in queryDocumentSnapshots.documents) {
                if (d.exists()) {
                    val mensaje = d.toObject(Mensaje::class.java)
                    mensajeArrayList.add(mensaje)
                }
            }
            if (mensajeArrayList.size == 0) {
                mensajeArrayList.add(mensaje)
            }
            mensajeArrayList.reverse()
            val gson = Gson()
            val mensajes = gson.toJson(mensajeArrayList)
            enviarNotificacion(token, mensajes, mensaje)
        }
    }

    private fun enviarNotificacion(token: String, mensajes: String, mensaje: Mensaje) {
        val data: MutableMap<String, String> = HashMap()
        data["title"] = getString(R.string.mensaje_nuevo)
        data["body"] = mensaje.mensaje
        data["idNotification"] = idNotificationChat.toString()
        data["mensajes"] = mensajes
        data["usernameSender"] = miNombreDeUsuario
        data["imagenSender"] = imagenSender
        data["idSender"] = mensaje.idSender
        data["idReceiver"] = mensaje.idReceiver
        data["idChat"] = mensaje.idChat
        val body = FCMBody(token, "high", "4500s", data)
        notificationProvider.sendNotification(body).enqueue(object : Callback<FCMResponse?> {
            override fun onResponse(call: Call<FCMResponse?>, response: Response<FCMResponse?>) {}
            override fun onFailure(call: Call<FCMResponse?>, t: Throwable) {}
        })
    }

    private fun getMyInfoUser() {
        userProvider.getUser(authProvider.uid).addOnSuccessListener { documentSnapshot ->
            if (documentSnapshot.exists()) {
                if (documentSnapshot.contains("username")) {
                    miNombreDeUsuario = documentSnapshot.getString("username")!!
                }
                if (documentSnapshot.contains("urlPerfil")) {
                    imagenSender = documentSnapshot.getString("urlPerfil").toString()
                }
            }
        }
    }

    override fun onPause() {
        super.onPause()
        ViewedMessageHelper.updateOnline(false, this@ActivityChat)
    }

    inner private class Register : AdapterDataObserver() {
        override fun onItemRangeInserted(positionStart: Int, itemCount: Int) {
            super.onItemRangeInserted(positionStart, itemCount)
            if (register != null) {
                updateViewed()
            }
            val numberMessage: Int? = mAdapter?.itemCount
            val lastMessagePosition: Int = linearLayoutManager.findLastCompletelyVisibleItemPosition()
            if (numberMessage != null) {
                if (lastMessagePosition == -1 || positionStart >= numberMessage - 1 && lastMessagePosition == positionStart - 1) {
                    recyclerViewMensajes.scrollToPosition(positionStart)
                }
            }
        }
    }
}
