package org.gms.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;
import org.gms.constants.api.ApiConstant;
import org.gms.model.dto.CharacterDollReqDTO;
import org.gms.model.dto.CharacterDollRtnDTO;
import org.gms.model.dto.ResultBody;
import org.gms.model.dto.SubmitBody;
import org.gms.service.doll.CharacterDollService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 人偶 REST：本地 {@code Character.wz} 合成 WZ 1:1 人偶，供座椅/坐骑预览与装备试穿复用。
 * <p>
 * 管理端 UI 挂在「客户端」菜单（menu.client / client.ts）。
 */
@RestController
@AllArgsConstructor
@RequestMapping("/characterDoll")
public class CharacterDollController {
    private final CharacterDollService characterDollService;

    @Tag(name = "/characterDoll/" + ApiConstant.LATEST)
    @Operation(summary = "合成人偶（本地 WZ 1:1）")
    @PostMapping("/" + ApiConstant.LATEST + "/render")
    public ResultBody<CharacterDollRtnDTO> render(@RequestBody SubmitBody<CharacterDollReqDTO> request) {
        CharacterDollReqDTO data = request.getData() == null ? new CharacterDollReqDTO() : request.getData();
        return ResultBody.success(request, characterDollService.render(data));
    }
}
