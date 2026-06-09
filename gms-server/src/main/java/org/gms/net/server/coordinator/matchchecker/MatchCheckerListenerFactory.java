/*
    This file is part of the HeavenMS MapleStory Server
    Copyleft (L) 2016 - 2019 RonanLana

    This program is free software: you can redistribute it and/or modify
    it under the terms of the GNU Affero General Public License as
    published by the Free Software Foundation version 3 as published by
    the Free Software Foundation. You may not use, modify or distribute
    this program under any other version of the GNU Affero General Public
    License.

    This program is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU Affero General Public License for more details.

    You should have received a copy of the GNU Affero General Public License
    along with this program.  If not, see <http://www.gnu.org/licenses/>.
*/
package org.gms.net.server.coordinator.matchchecker;

import org.gms.net.server.coordinator.matchchecker.listener.MatchCheckerCPQChallenge;
import org.gms.net.server.coordinator.matchchecker.listener.MatchCheckerGuildCreation;

/**
 * 匹配检查器监听器工厂
 * 根据匹配检查类型创建对应的监听器实例
 */
public class MatchCheckerListenerFactory {

    /**
     * 匹配检查类型枚举
     */
    public enum MatchCheckerType {

        GUILD_CREATION(MatchCheckerGuildCreation.loadListener()),
        CPQ_CHALLENGE(MatchCheckerCPQChallenge.loadListener());

        private final AbstractMatchCheckerListener listener;

        MatchCheckerType(AbstractMatchCheckerListener listener) {
            this.listener = listener;
        }

        public AbstractMatchCheckerListener getListener() {
            return this.listener;
        }
    }

}