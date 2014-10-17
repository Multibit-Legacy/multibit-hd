#!/bin/sh
echo Move into position...
mkdir /opt/multibit-hd
cp mbhd-$1-linux /opt/multibit-hd/multibit-hd
cp trademark-logo.png /usr/share/icons/hicolor/128x128/apps/multibit-hd.png
chmod +x /opt/multibit-hd/multibit-hd

echo Create symlink...
ln -s /opt/multibit-hd/multibit-hd /usr/bin/multibit-hd

echo Activate HID USB...
cat << _EOF_ > /etc/udev/rules.d/99-multibit-hd.rules
# Trezor HID device
ATTRS{idProduct}=="0001", ATTRS{idVendor}=="534c", MODE="0660", GROUP="plugdev"
_EOF_

echo Build Unity desktop...
cat << _EOF_ > multibit-hd.desktop
[Desktop Entry]
Version=1.0
Type=Application
Terminal=false
Exec=/opt/multibit-hd/multibit-hd %U
Icon=/usr/share/icons/hicolor/128x128/apps/multibit-hd.png
Name[en_US]=MultiBit HD
Comment=Secure lightweight international Bitcoin wallet
Comment[en_US]=Secure lightweight international Bitcoin wallet
Name=MultiBit HD
Categories=Network;X-Bitcoin;
MimeType=x-scheme-handler/bitcoin;
_EOF_

echo Install to Unity...
desktop-file-validate multibit-hd.desktop
desktop-file-install multibit-hd.desktop
update-desktop-database

echo Done. Please check the Unity dock.
