#!/system/bin/sh
# =============================================================================
# Diagnóstico de eSIM / eUICC no TBox do Haval
# =============================================================================
# 100% READ-ONLY. Nenhum comando abaixo ativa, baixa, apaga ou troca perfil.
# Objetivo: descobrir se o modem de conectividade (TBox) tem um eUICC
# programável (aceita download de perfil LPA) ou um SIM físico/soldado.
#
# Como rodar (via Shizuku/adb shell, no ambiente do carro):
#   sh /sdcard/esim-diagnostico.sh 2>&1 | tee /sdcard/esim-diag.log
# Depois copie o esim-diag.log de volta para análise.
# =============================================================================

echo "################ INÍCIO DIAGNÓSTICO eSIM ################"
echo "Data: $(date)"
echo "Android: $(getprop ro.build.version.release)  SDK: $(getprop ro.build.version.sdk)"
echo

echo "===== 1. Hardware de telefonia declarado ====="
pm list features 2>/dev/null | grep -iE "telephony|euicc|subscription"
echo

echo "===== 2. Serviço euicc existe no sistema? ====="
# Se 'euicc' aparecer, existe uma implementação LPA no dispositivo.
service list 2>/dev/null | grep -iE "euicc|isub|phone|carrier"
echo

echo "===== 3. cmd euicc — capacidades do eUICC ====="
# Em muitos builds isso lista o eid e se há suporte.
cmd euicc 2>&1 | head -40
echo "--- get-eid (identificador do chip eUICC, se houver) ---"
cmd euicc get-eid 2>&1
echo

echo "===== 4. dumpsys euicc (LPA / SM-DP+ / perfis) ====="
dumpsys euicc 2>&1 | head -80
echo

echo "===== 5. dumpsys isub / SubscriptionController ====="
# Mostra subscriptions ativas: ICCID, carrier, se é embedded (eSIM) ou não.
dumpsys isub 2>&1 | head -60
echo

echo "===== 6. Estado do modem via telephony.registry ====="
dumpsys telephony.registry 2>&1 | grep -iE "mServiceState|mSimState|mSubId|carrier|mNetworkType|mDataConnectionState" | head -40
echo

echo "===== 7. Propriedades de rádio/modem relevantes ====="
getprop | grep -iE "gsm\.|ril\.|persist\.radio|telephony|euicc|esim" | head -60
echo

echo "===== 8. Pacotes LPA / operadora instalados ====="
pm list packages 2>/dev/null | grep -iE "euicc|lpa|esim|simapp|carrier|telephonyprovider|autolink|tbox"
echo

echo "===== 9. Serviço TBox da Autolink (o do app) ====="
service list 2>/dev/null | grep -iE "tbox|autolink"
dumpsys activity service com.autolink 2>&1 | head -30
echo

echo "################ FIM DIAGNÓSTICO ################"
