/*
    This file is part of the HeavenMS MapleStory Server, commands OdinMS-based
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

/*
   @Author: Arthur L - Refactored command content into modules
*/
package org.gms.client.command.commands.gm0;

import org.gms.client.Client;
import org.gms.client.command.Command;
import org.gms.util.DatabaseConnection;
import org.gms.util.I18nUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class ChangeLanguageCommand extends Command {
    private static final Logger log = LoggerFactory.getLogger(ChangeLanguageCommand.class);

    {
        setDescription(I18nUtil.getMessage("ChangeLanguageCommand.message1"));
    }

    @Override
    public void execute(Client c, String[] params) {
        if (params.length < 1) {
            c.getPlayer().yellowMessage(I18nUtil.getMessage("ChangeLanguageCommand.message2"));
            return;
        }
        int lang;
        try {
            lang = Integer.parseInt(params[0]);
        } catch (NumberFormatException e) {
            c.getPlayer().yellowMessage(I18nUtil.getMessage("ChangeLanguageCommand.message2"));
            return;
        }
        c.setLanguage(lang);
        persistLanguage(c.getAccID(), lang);
        c.getPlayer().yellowMessage(I18nUtil.getMessage("ChangeLanguageCommand.message3"));
    }

    // 把语言偏好回写 accounts.language，避免重连复位，使运行时切换真正闭环
    private void persistLanguage(int accountId, int lang) {
        try (Connection con = DatabaseConnection.getConnection();
             PreparedStatement ps = con.prepareStatement("UPDATE accounts SET language = ? WHERE id = ?")) {
            ps.setInt(1, lang);
            ps.setInt(2, accountId);
            ps.executeUpdate();
        } catch (SQLException e) {
            log.error("Failed to persist language for account {}", accountId, e);
        }
    }
}
