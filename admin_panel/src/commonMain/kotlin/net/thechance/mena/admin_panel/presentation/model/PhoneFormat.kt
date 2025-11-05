package net.thechance.mena.admin_panel.presentation.model

data class PhoneFormat(
    val callingCode: String, val masks: Map<Int, String>
)

val menaPhoneFormats = listOf(
    PhoneFormat("+213", mapOf(10 to "## ### ## ##")),
    PhoneFormat("+973", mapOf(8 to "#### ####")),
    PhoneFormat("+20", mapOf(10 to "### ### ####")),
    PhoneFormat("+98", mapOf(10 to "### ### ####")),
    PhoneFormat("+964", mapOf(10 to "### ### ####")),
    PhoneFormat("+962", mapOf(9 to "# #### ####")),
    PhoneFormat("+965", mapOf(8 to "#### ####")),
    PhoneFormat("+961", mapOf(8 to "# ### ###")),
    PhoneFormat("+218", mapOf(10 to "## ### ####")),
    PhoneFormat("+212", mapOf(9 to "### ### ###")),
    PhoneFormat("+968", mapOf(8 to "#### ####")),
    PhoneFormat("+970", mapOf(9 to "### ### ###")),
    PhoneFormat("+974", mapOf(8 to "#### ####")),
    PhoneFormat("+966", mapOf(9 to "# #### ####")),
    PhoneFormat("+252", mapOf(8 to "# ### ###")),
    PhoneFormat("+249", mapOf(9 to "## ### ####")),
    PhoneFormat("+963", mapOf(9 to "## ### ###")),
    PhoneFormat("+216", mapOf(8 to "## ### ###")),
    PhoneFormat("+971", mapOf(9 to "# ### ####")),
    PhoneFormat("+967", mapOf(9 to "# ### ###"))
)