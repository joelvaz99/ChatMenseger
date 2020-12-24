package ipvc.estg.chatmenseger.ModelClasse

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class Group(
    val groupId: String = "",
    val groupDescription: String  = "",
    val groupTitle: String  = "",
    val createdBy: String  = "",
    val url: String = "" ): Parcelable