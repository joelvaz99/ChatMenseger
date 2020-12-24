package ipvc.estg.chatmenseger.ModelClasse

import java.sql.Timestamp

data class MessageGroup (val message: String = "",
                     val messageId: String = "",
                     val sender: String = "",
                     val timestamp: String = ""
)