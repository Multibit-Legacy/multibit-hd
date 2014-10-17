#!/bin/sh
echo Remove MultiBit HD...
rm -Rf /opt/multibit-hd
rm -Rf "~/.JWrapper/JWrapper-MultiBit HD"

echo Remove symlink...
rm -f /usr/bin/multibit-hd

echo Remove HID USB rules...
rm -f /etc/udev/rules.d/99-multibit-hd.rules

echo Remove from Unity...
rm -f /usr/share/applications/multibit-hd.desktop
rm -f ~/.local/share/applications/multibit-hd.desktop
rm -f /usr/local/share/applications/multibit-hd.desktop
update-desktop-database

echo MultiBit HD application is removed. 
echo Your wallet files remain intact in ~/.MultiBitHD
echo Your backup files remain inact. 
