var status = 0;

function start() {
    cm.sendNextS("I'm not walking outta here until I clear this mess up!", 3);
}

function action(mode, type, selection) {
    status++;
    switch (status) {
        case 1:
            cm.sendNextPrev("This whole thing stinks of jealousy! We're the only commoners with a direct line to the king. I guarantee that put a thorn under somebody's seat. Some blasted fool out there wants to put a smudge on the name of every bounty hunter in the galaxy!", 9);
            break;
        case 2:
            cm.sendNextPrev("You think it was an inside job? Someone at the palace?", 3);
            break;
        case 3:
            cm.getDirectionInfo(1, 1000);
            cm.getDirectionInfo("Effect/DirectionNewPirate.img/newPirate/balloonMsg2/11", 2000, 0, -100, 0, 0);
            cm.getDirectionInfo(1, 500);
            cm.sendNextPrev("I guess they didn't think too hard about who they picked to run a scheme on... Somebody's gonna pay for ruining my day.", 3);
            break;
        case 4:
            cm.sendNextPrev("We can figure out who to work on later. Somebody had access to the king and somebody took him down. How many people do you think could get through those defenses besides us?", 9);
            break;
        case 5:
            cm.sendNextPrev("I guarantee that's what everybody's gonna think. No matter how hard we try to convince these jackboots that we were trying to help, they're just gonna see an outsider with a little too much blood on their hands. You have to escape and you have to do it right now.", 9);
            break;
        case 6:
            cm.showJettWanted();
            break;
    }
}