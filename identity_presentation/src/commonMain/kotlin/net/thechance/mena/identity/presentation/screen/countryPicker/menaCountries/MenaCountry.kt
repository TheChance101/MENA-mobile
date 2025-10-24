package net.thechance.mena.identity.presentation.screen.countryPicker.menaCountries

import mena.identity_presentation.generated.resources.Res
import mena.identity_presentation.generated.resources.ae_flag
import mena.identity_presentation.generated.resources.algeria
import mena.identity_presentation.generated.resources.bahrain
import mena.identity_presentation.generated.resources.bh_flag
import mena.identity_presentation.generated.resources.dz_flag
import mena.identity_presentation.generated.resources.eg_flag
import mena.identity_presentation.generated.resources.egypt
import mena.identity_presentation.generated.resources.iq_flag
import mena.identity_presentation.generated.resources.ir_flag
import mena.identity_presentation.generated.resources.iran
import mena.identity_presentation.generated.resources.iraq
import mena.identity_presentation.generated.resources.jo_flag
import mena.identity_presentation.generated.resources.jordan
import mena.identity_presentation.generated.resources.kuwait
import mena.identity_presentation.generated.resources.kw_flag
import mena.identity_presentation.generated.resources.lb_flag
import mena.identity_presentation.generated.resources.lebanon
import mena.identity_presentation.generated.resources.libya
import mena.identity_presentation.generated.resources.ly_flag
import mena.identity_presentation.generated.resources.ma_flag
import mena.identity_presentation.generated.resources.morocco
import mena.identity_presentation.generated.resources.om_flag
import mena.identity_presentation.generated.resources.oman
import mena.identity_presentation.generated.resources.palestine
import mena.identity_presentation.generated.resources.ps_flag
import mena.identity_presentation.generated.resources.qa_flag
import mena.identity_presentation.generated.resources.qatar
import mena.identity_presentation.generated.resources.sa_flag
import mena.identity_presentation.generated.resources.saudi_arabia
import mena.identity_presentation.generated.resources.sd_flag
import mena.identity_presentation.generated.resources.so_flag
import mena.identity_presentation.generated.resources.somalia
import mena.identity_presentation.generated.resources.sudan
import mena.identity_presentation.generated.resources.sy_flag
import mena.identity_presentation.generated.resources.syria
import mena.identity_presentation.generated.resources.tn_flag
import mena.identity_presentation.generated.resources.tunisia
import mena.identity_presentation.generated.resources.uae
import mena.identity_presentation.generated.resources.ye_flag
import mena.identity_presentation.generated.resources.yemen
import org.jetbrains.compose.resources.DrawableResource
import org.jetbrains.compose.resources.StringResource

enum class MenaCountry(
    val countryNameRes: StringResource,
    val callingCode: String,
    val countryCodeName: String,
    val flagImage: DrawableResource
) {

    ALGERIA(Res.string.algeria, "+213", "DZ", Res.drawable.dz_flag),
    BAHRAIN(Res.string.bahrain, "+973", "BH", Res.drawable.bh_flag),
    EGYPT(Res.string.egypt, "+20", "EG", Res.drawable.eg_flag),
    IRAN(Res.string.iran, "+98", "IR", Res.drawable.ir_flag),
    IRAQ(Res.string.iraq, "+964", "IQ", Res.drawable.iq_flag),
    JORDAN(Res.string.jordan, "+962", "JO", Res.drawable.jo_flag),
    KUWAIT(Res.string.kuwait, "+965", "KW", Res.drawable.kw_flag),
    LEBANON(Res.string.lebanon, "+961", "LB", Res.drawable.lb_flag),
    LIBYA(Res.string.libya, "+218", "LY", Res.drawable.ly_flag),
    MOROCCO(Res.string.morocco, "+212", "MA", Res.drawable.ma_flag),
    OMAN(Res.string.oman, "+968", "OM", Res.drawable.om_flag),
    PALESTINE(Res.string.palestine, "+970", "PS", Res.drawable.ps_flag),
    QATAR(Res.string.qatar, "+974", "QA", Res.drawable.qa_flag),
    SAUDI_ARABIA(Res.string.saudi_arabia, "+966", "SA", Res.drawable.sa_flag),
    SOMALIA(Res.string.somalia, "+252", "SO", Res.drawable.so_flag),
    SUDAN(Res.string.sudan, "+249", "SD", Res.drawable.sd_flag),
    SYRIA(Res.string.syria, "+963", "SY", Res.drawable.sy_flag),
    TUNISIA(Res.string.tunisia, "+216", "TN", Res.drawable.tn_flag),
    UAE(Res.string.uae, "+971", "AE", Res.drawable.ae_flag),
    YEMEN(Res.string.yemen, "+967", "YE", Res.drawable.ye_flag);


    fun getFormatedCountryCode(isRtl: Boolean) =
        if (isRtl) callingCode.drop(1) + "+" else callingCode
}
