#!/bin/bash
#
# Expected shell script location: “Idea Projects”/multibit-hd/mbhd-swing/src/main/resources/languages-from-crowdin
#
# Move the localisation files downloaded from Crowdin (http://translate.multibit.org) 
# into the format used by MultiBit HD
# These latter are all in the format: language_af_AF.properties
#
# If you add languages to the project you will have to extend this shell script

# Afrikaans
mv -f ./multibit-hd/af/language.properties ../languages/language_af_AF.properties
rmdir ./multibit-hd/af/

# Arabic 
mv -f ./multibit-hd/ar/language.properties ../languages/language_ar_AR.properties
rmdir ./multibit-hd/ar/

# Bulgarian
mv -f ./multibit-hd/bg/language.properties ../languages/language_bg_BG.properties
rmdir ./multibit-hd/bg/

# Catalan
mv -f ./multibit-hd/ca/language.properties ../languages/language_ca_ES.properties
rmdir ./multibit-hd/ca/

# Czech
mv -f ./multibit-hd/cs/language.properties ../languages/language_cs_CZ.properties
rmdir ./multibit-hd/cs/

# Danish
mv -f ./multibit-hd/da/language.properties ../languages/language_da_DK.properties
rmdir ./multibit-hd/da/

# German
mv -f ./multibit-hd/de/language.properties ../languages/language_de_DE.properties
rmdir ./multibit-hd/de/ 

# Greek 
mv -f ./multibit-hd/el/language.properties ../languages/language_el_GR.properties
rmdir ./multibit-hd/el/

# English English
mv -f ./multibit-hd/en/language.properties ../languages/language_en_EN.properties
rmdir ./multibit-hd/en/

# American English 
mv -f ./multibit-hd/en-US/language.properties ../languages/language_en_US.properties
rmdir ./multibit-hd/en-US/

# Esperanto
mv -f ./multibit-hd/eo/language.properties ../languages/language_eo.properties
rmdir ./multibit-hd/eo/ 

# Spanish Spanish
mv -f ./multibit-hd/es-ES/language.properties ../languages/language_es_ES.properties
rmdir ./multibit-hd/es-ES/

# Farsi / Persian
mv -f ./multibit-hd/fa/language.properties ../languages/language_fa_IR.properties
rmdir ./multibit-hd/fa/

# Finnish
mv -f ./multibit-hd/fi/language.properties ../languages/language_fi_FI.properties
rmdir ./multibit-hd/fi/

# French
mv -f ./multibit-hd/fr/language.properties ../languages/language_fr_FR.properties
rmdir ./multibit-hd/fr/

# Hebrew
mv -f ./multibit-hd/he/language.properties ../languages/language_iw_IL.properties
rmdir ./multibit-hd/he/

# Hindi
mv -f ./multibit-hd/hi/language.properties ../languages/language_hi_IN.properties
rmdir ./multibit-hd/hi/

# Croatian
mv -f ./multibit-hd/hr/language.properties ../languages/language_hr_HR.properties
rmdir ./multibit-hd/hr/

# Hungarian
mv -f ./multibit-hd/hu/language.properties ../languages/language_hu_HU.properties
rmdir ./multibit-hd/hu/

# Indonesian
mv -f ./multibit-hd/id/language.properties ../languages/language_in_ID.properties
rmdir ./multibit-hd/id/

# Italian
mv -f ./multibit-hd/it/language.properties ../languages/language_it_IT.properties
rmdir ./multibit-hd/it/

# Japanese
mv -f ./multibit-hd/ja/language.properties ../languages/language_hr_HR.properties
rmdir ./multibit-hd/ja/

# Korean
mv -f ./multibit-hd/ko/language.properties ../languages/language_ko_KR.properties
rmdir ./multibit-hd/ko/

# Latvian
mv -f ./multibit-hd/lt/language.properties ../languages/language_lt_LT.properties
rmdir ./multibit-hd/lt/

# Lithuanian
mv -f ./multibit-hd/lv/language.properties ../languages/language_lv_LV.properties
rmdir ./multibit-hd/lv/

# Mongolian
mv -f ./multibit-hd/mn/language.properties ../languages/language_mn_MN.properties
rmdir ./multibit-hd/mn/

# Dutch
mv -f ./multibit-hd/nl/language.properties ../languages/language_nl_NL.properties
rmdir ./multibit-hd/nl/

# Norwegian
mv -f ./multibit-hd/no/language.properties ../languages/language_no_NO.properties
rmdir ./multibit-hd/no/

# Polish
mv -f ./multibit-hd/pl/language.properties ../languages/language_pl_PL.properties
rmdir ./multibit-hd/pl/

# Portuguese Portuguese
mv -f ./multibit-hd/pt-PT/language.properties ../languages/language_pt_PT.properties
rmdir ./multibit-hd/pt-PT/

# Brazilian Portuguese
mv -f ./multibit-hd/pt-BR/language.properties ../languages/language_pt_BR.properties
rmdir ./multibit-hd/pt-BR/

# Romanian
mv -f ./multibit-hd/ro/language.properties ../languages/language_ro_RO.properties
rmdir ./multibit-hd/ro/

# Russian
mv -f ./multibit-hd/ru/language.properties ../languages/language_ru_RU.properties
rmdir ./multibit-hd/ru/

# Slovakian
mv -f ./multibit-hd/sk/language.properties ../languages/language_sk_SK.properties
rmdir ./multibit-hd/sk/

# Slovenian
mv -f ./multibit-hd/sl/language.properties ../languages/language_sl_SI.properties
rmdir ./multibit-hd/sl/

# Swahili
mv -f ./multibit-hd/sw/language.properties ../languages/language_sw_KE.properties
rmdir ./multibit-hd/sw/

# Serbian
mv -f ./multibit-hd/sr-CS/language.properties ../languages/language_sr_CS.properties
rmdir ./multibit-hd/sr-CS/

# Swedish
mv -f ./multibit-hd/sv-SE/language.properties ../languages/language_sv_SV.properties
rmdir ./multibit-hd/sv-SE/

# Tamil
mv -f ./multibit-hd/ta/language.properties ../languages/language_ta_LK.properties
rmdir ./multibit-hd/ta/

# Thai
mv -f ./multibit-hd/th/language.properties ../languages/language_th_TH.properties
rmdir ./multibit-hd/th/

# Tagalog
mv -f ./multibit-hd/tl/language.properties ../languages/language_tl_PH.properties
rmdir ./multibit-hd/tl/

# Turkish
mv -f ./multibit-hd/tr/language.properties ../languages/language_tr_TR.properties
rmdir ./multibit-hd/tr/

# Vietnamese
mv -f ./multibit-hd/vi/language.properties ../languages/language_vi_VN.properties
rmdir ./multibit-hd/vi/

# Simplified Chinese
mv -f ./multibit-hd/zh-CN/language.properties ../languages/language_zh_CN.properties
rmdir ./multibit-hd/zh-CN/

# Traditional Chinese
mv -f ./multibit-hd/zh-TW/language.properties ../languages/language_zh_TW.properties
rmdir ./multibit-hd/zh-TW/

























echo ————————
echo If you have any files listed here, you need to add your language to “move-files.sh” !
ls ./multibit-hd/*.*