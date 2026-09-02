
function action(mode, type, selection) {
    if (mode == 1) {
			im.teachSkill(5111005,20);
            im.getPlayer().changeKeybinding(20,1,5111005);
    }
    im.dispose();
}
