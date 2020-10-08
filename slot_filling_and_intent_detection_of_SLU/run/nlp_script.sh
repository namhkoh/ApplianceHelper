
#!/bin/bash



file_start="https://www.dropbox.com/sh/2z0qu0biwkwf0k0/AAC07j3w7uf_n03pOtmTosWPa/text_"

file_start="https://www.dropbox.com/sh/2z0qu0biwkwf0k0/AADbeHb58um_C5kcEAkDfAfQa/text_"

https="https://"

file_var="10-01"

file_end="?dl=0&subfolder_nav_tracking=1/"

file_name="${file_start}${file_var}${file_end}"

text_="downloads/text_"



strings=(
	"https://www.dropbox.com/sh/2z0qu0biwkwf0k0/AAC07j3w7uf_n03pOtmTosWPa/text_10-01?dl=0&subfolder_nav_tracking=1"
	"https://www.dropbox.com/sh/2z0qu0biwkwf0k0/AADbeHb58um_C5kcEAkDfAfQa/text_10-02?dl=0&subfolder_nav_tracking=1"
	"https://www.dropbox.com/sh/2z0qu0biwkwf0k0/AADi6P-WIqU8u3hTmUs8_j1ua/text_10-03?dl=0&subfolder_nav_tracking=1"
	"https://www.dropbox.com/sh/2z0qu0biwkwf0k0/AACpsY3jjrn9YZZxwOpTsComa/text_10-04?dl=0&subfolder_nav_tracking=1"
	"https://www.dropbox.com/sh/2z0qu0biwkwf0k0/AAAdSiFMrkcQtWecPHWcN9JMa/text_10-05?dl=0&subfolder_nav_tracking=1"
	"https://www.dropbox.com/sh/2z0qu0biwkwf0k0/AACWdcv5hsEqqEeZ4R2ffJrpa/text_10-06?dl=0&subfolder_nav_tracking=1"
	"https://www.dropbox.com/sh/2z0qu0biwkwf0k0/AAA0bRnPWdErxSYPo5ZO-voia/text_10-07?dl=0&subfolder_nav_tracking=1"
	"https://www.dropbox.com/sh/2z0qu0biwkwf0k0/AADbNvS_DbKeeZPrrAD7ovT2a/text_10-08?dl=0&subfolder_nav_tracking=1"
	"https://www.dropbox.com/sh/2z0qu0biwkwf0k0/AAAv2t2Txe-0FKSUYiQ3fB37a/text_10-09?dl=0&subfolder_nav_tracking=1"
	"https://www.dropbox.com/sh/2z0qu0biwkwf0k0/AACixXqIEtsuqxNHvO6qNr7Wa/text_10-10?dl=0&subfolder_nav_tracking=1"
	"https://www.dropbox.com/sh/2z0qu0biwkwf0k0/AAC2HzGGDVV1ICmKAokRJHeJa/text_10-11?dl=0&subfolder_nav_tracking=1"
	"https://www.dropbox.com/sh/2z0qu0biwkwf0k0/AAAfgUCyWm8YiMlEcARGgLSga/text_10-12?dl=0&subfolder_nav_tracking=1"
)

strings_2=(
	"10-01.zip"
	"10-02.zip"
	"10-03.zip"
	"10-04.zip"
	"10-05.zip"
	"10-06.zip"
	"10-07.zip"
	"10-08.zip"
	"10-09.zip"
	"10-10.zip"
	"10-11.zip"
	"10-12.zip"
)



for ((i=0;i<${#strings[@]};++i)); do
	echo "${strings[i]} ${strings_2[i]}"
	wget --max-redirect=20 -O "${text_}${strings_2[i]}" "${strings[i]}"
done