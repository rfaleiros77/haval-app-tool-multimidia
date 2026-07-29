package br.com.redesurftank.havalshisuku.managers

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.telephony.euicc.DownloadableSubscription
import android.telephony.euicc.EuiccManager
import android.util.Log
import br.com.redesurftank.App

/**
 * Encapsula o fluxo OFICIAL de provisionamento de eSIM do Android (EuiccManager / LPA).
 *
 * IMPORTANTE — leia antes de usar:
 *  - Isto SÓ funciona se o eUICC (chip eSIM programável) estiver acessível ao Android da
 *    multimídia, ou seja, se `EuiccManager.isEnabled == true` e houver um EID válido.
 *    Em muitos Havais o modem fica em um TBox separado e o eSIM NÃO é visível aqui —
 *    nesse caso `isEuiccAvailable()` retorna false e não há nada que este manager possa fazer.
 *  - O download de perfil exige um código de ativação (LPA:1$SMDP$MATCHINGID) fornecido
 *    PELA OPERADORA para um plano que você contratou. Este código dispara o download do
 *    perfil legítimo junto ao servidor SM-DP+ da operadora. Não há aqui (nem deve haver)
 *    clonagem de ICCID/IMSI, bypass de lock de operadora ou forja de credencial.
 *
 * O diagnóstico (DOCS/esim-diagnostico.sh) deve confirmar o eUICC ANTES de habilitar
 * este fluxo na UI.
 */
object EsimManager {
    private const val TAG = "EsimManager"
    private const val ACTION_DOWNLOAD_RESULT = "br.com.redesurftank.havalshisuku.ESIM_DOWNLOAD_RESULT"

    private val euiccManager: EuiccManager? by lazy {
        try {
            App.getContext().getSystemService(Context.EUICC_SERVICE) as? EuiccManager
        } catch (e: Exception) {
            Log.e(TAG, "EuiccManager indisponível", e)
            null
        }
    }

    /** true se existe um eUICC programável acessível a ESTE Android (multimídia). */
    fun isEuiccAvailable(): Boolean {
        val mgr = euiccManager ?: return false
        return try {
            mgr.isEnabled
        } catch (e: Exception) {
            Log.e(TAG, "Erro consultando isEnabled", e)
            false
        }
    }

    /** EID do eUICC (32 dígitos) ou null. Útil para confirmar hardware no diagnóstico. */
    fun getEid(): String? {
        val mgr = euiccManager ?: return null
        if (!isEuiccAvailable()) return null
        return try {
            mgr.eid
        } catch (e: SecurityException) {
            // Requer READ_PRIVILEGED_PHONE_STATE; via Shizuku/priv-app pode estar disponível.
            Log.e(TAG, "Sem permissão para ler EID", e)
            null
        } catch (e: Exception) {
            Log.e(TAG, "Erro lendo EID", e)
            null
        }
    }

    /**
     * Dispara o download de um perfil eSIM a partir de um código de ativação da operadora.
     *
     * @param activationCode string no formato "LPA:1$<smdp>$<matchingId>" (o mesmo conteúdo
     *        do QR code que a operadora fornece).
     * @param onResult callback com (sucesso, mensagem). Chamado a partir de um BroadcastReceiver.
     *
     * NÃO chame isto sem antes validar isEuiccAvailable(). O download é uma operação que
     * ALTERA o estado de conectividade do carro — deve ser precedido de confirmação explícita
     * do usuário na UI.
     */
    fun downloadProfile(activationCode: String, onResult: (Boolean, String) -> Unit) {
        val mgr = euiccManager
        if (mgr == null || !isEuiccAvailable()) {
            onResult(false, "eUICC não disponível nesta multimídia (provável eSIM no TBox).")
            return
        }
        val code = activationCode.trim()
        if (!code.startsWith("LPA:", ignoreCase = true) && !code.startsWith("1$")) {
            onResult(false, "Código de ativação inválido. Esperado formato LPA:1\$...")
            return
        }

        val context = App.getContext()
        val receiver = object : BroadcastReceiver() {
            override fun onReceive(ctx: Context, intent: Intent) {
                try {
                    ctx.unregisterReceiver(this)
                } catch (_: Exception) {
                }
                val resultCode = resultCode
                if (resultCode == EuiccManager.EMBEDDED_SUBSCRIPTION_RESULT_OK) {
                    Log.w(TAG, "Download de perfil eSIM concluído com sucesso")
                    onResult(true, "Perfil eSIM baixado e ativado com sucesso.")
                } else {
                    val detailed = intent.getIntExtra(
                        EuiccManager.EXTRA_EMBEDDED_SUBSCRIPTION_DETAILED_CODE, 0
                    )
                    Log.e(TAG, "Falha no download eSIM. resultCode=$resultCode detailed=$detailed")
                    onResult(false, "Falha ao baixar perfil (código $resultCode / detalhe $detailed).")
                }
            }
        }
        context.registerReceiver(
            receiver,
            IntentFilter(ACTION_DOWNLOAD_RESULT),
            Context.RECEIVER_NOT_EXPORTED
        )

        try {
            val sub = DownloadableSubscription.forActivationCode(code)
            val callbackIntent = android.app.PendingIntent.getBroadcast(
                context,
                0,
                Intent(ACTION_DOWNLOAD_RESULT).setPackage(context.packageName),
                android.app.PendingIntent.FLAG_UPDATE_CURRENT or android.app.PendingIntent.FLAG_MUTABLE
            )
            // switchAfterDownload=true → ativa o perfil recém-baixado.
            mgr.downloadSubscription(sub, /* switchAfterDownload = */ true, callbackIntent)
            Log.w(TAG, "Solicitação de download de perfil eSIM enviada ao LPA")
        } catch (e: Exception) {
            try {
                context.unregisterReceiver(receiver)
            } catch (_: Exception) {
            }
            Log.e(TAG, "Erro ao solicitar download de perfil", e)
            onResult(false, "Erro ao solicitar download: ${e.message}")
        }
    }
}
