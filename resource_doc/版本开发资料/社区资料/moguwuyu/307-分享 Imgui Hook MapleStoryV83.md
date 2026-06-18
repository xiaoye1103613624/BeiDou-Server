# 分享 Imgui Hook MapleStoryV83

> 来源：https://moguwuyu.com/d/307
> 站点：蘑菇物语(moguwuyu.com) · Flarum 改端技术论坛

**#1楼**

前言: 之前在CSDN发布过这篇文章,之所以转回蘑菇物语是因为之前的蘑菇物语不支持markdowm,对长篇文章的支持并不友好 哈哈哈
我在文章的最后也不充了如何修复之前发布的代码存在的一个bug,即游戏分辨率切换的时候会导致的游戏界面卡死的问题,核心原因是没有处理IDrect3DDevice9::Reset,应该补充上这部分的hook.
为了方便大家食用,我在最后附上了一个解决掉切换分辨率会闪退的完整imgui hook代码.
1. Imgui 的简介与尝试
ImGui（全称 Dear ImGui）是一个 即时模式图形用户界面库（Immediate Mode GUI），主要用来在应用程序或游戏中快速开发调试工具、编辑器和可视化界面。它由 Omar Cornut 开发并持续维护，在游戏开发、图形引擎调试和科研可视化中被广泛使用。
可以通过ImGui提供的Github地址进行下载
https://github.com/ocornut/imgui
。在本分享教程中所使用的版本为最新的imgui-1.92.2b, 全文中以imgui代称。
下载完成之后可以尝试跑一下imgui目录下examples文件夹中提供的一些例程，本文建议跑一下
example_win32_directx9
，感受一下什么是Imgui，顺便看一下Imgui的标准代码构成。关于Imgui相关的知识感兴趣的可以自己去学一下，不感兴趣的可以直接将代码贴给AI，由Imgui相对成熟，因此AI的解释详尽且准确。当你正确完成编译后应该能得到如下窗口：
2. 一些准备
众所周知，客户端是基于 DirectX 8（DX8）开发的。当时（2004
2007
年左右）的游戏大多使用 DirectX 7/8，你可以在 GMS083 客户端里找到 d3d8.dll 的加载依赖（也许不是这个名字），而不是 d3d9.dll。到了 v95
v112
以后，客户端才逐渐迁移到 DirectX 9。
由于ImgGui自带的相关例程中并未提供DirectX8的相关代码，所以我们如果想做 Hook 或 ImGui 注入的话，可能要先解决DirectX8 到DirectX9的问题。
幸运的是我又找到了一些有趣的资料，由大佬提供的一份代码，
https://github.com/iw2d/Gr2D_DX8to9
，项目的描述为：“Wrapper for Gr2D_DX9.dll from v95 to be used in v83“，即
把 v95 版本里的 Gr2D_DX9.dll 做一个封装（Wrapper），以便在 v83 版本中使用。
感兴趣的可以自己去编译，不感兴趣的直接把项目中release的文件（
Gr2D_DX8.dll
，
Gr2D_DX9.dll
）下载下来即可。
当然必不可少的是一个hook工具，这里我们可以选择现成的BeidouMS项目中Beidou-ijl15部分。
https://github.com/BeiDouMS/BeiDou-ijl15
，将源码下载下来尝试编译。
当顺利完成编译后应该能得到一个ijl15.dll文件
当顺利完成了imgui基本例程的编译，Gr2D_DX8.dll、Gr2D_DX9.dll的下载以及BeiDou-ijl15项目的编译后，即可开始尝试进行imgui hook。
3. Imgui库的一些配置
打开Beidou-ijl15的工程，进入到dllmain.cpp文件，找到动态链接库（DLL）的入口函数，即DllMain函数，然后删掉那些没有用的，保留一个ijl15::CreateHook()；即可：
接下来是imgui库的引入，重新去查看example_win32_directx9工程，将他工程中的文件都拷贝出来，先别管有没有用，拉出来再说。你可以在当前的Beidou-ijl15工程建立imgui和dx9文件夹，并把文件全部丢进去，类似下面这样：
记得在项目中添加一下相对的路径，在右键项目->属性->c/c++->附加包含目录中输入相对路径:
$(projectdir)\imgui
$(projectdir)\dx9
$(projectdir)
完成配置之后，你还可以在自己的项目中添加一个文件夹，来分类存放这些文件，右键新建一个筛选器，建立一个imgui，使它看起来整洁一些
引入这些我们可能要使用，但是也许并用不全的头文件，尝试进行编译：
#include <d3d9.h>
#include <detours.h>
#include <Windows.h>
#include <shellapi.h>
#include"imgui/imgui.h"
#include "dx9/imgui_impl_win32.h"
#include "dx9/imgui_impl_dx9.h"
当然也需要一些静态链接库：
#pragma comment(lib, "d3d9.lib")
#pragma comment(lib, "detours.lib")
在编译过程中你可能会发现，出现了一些莫名其妙的报错，诸如：“在查找预编译头时遇到意外的文件结尾。是否忘记了向源中添加“#include "stdafx.h"”? ”，别慌，这是正常的，右键点击你所引入的这一大堆文件里的cpp文件，把预编译关掉，不要嫌麻烦，一个个关掉就完事了，然后编译。
通常情况下，我们应该能够顺利完成项目的编译：
现在所有的配置已经完成，接下来就是正式进行imgui hook操作。
4. imgui hook
既然是hook，我们肯定要选择一个hook的函数，这里我们选择
IDirect3DDevice9
，为什么要选择这个？IDirect3DDevice9 是 DirectX9 渲染设备的核心接口，它管理所有图形渲染操作。在它提供的众多方法里，EndScene 是每一帧渲染的关键节点。
每帧都会调用：
游戏在每一帧渲染结束时，都会调用 IDirect3DDevice9::EndScene。
这是在调用 Present()（将渲染结果显示到屏幕）之前的最后一步。
这意味着在这里插入渲染代码，能保证每帧都能绘制 UI 或覆盖物。
渲染上下文可用：
EndScene 被调用时，Direct3D 的渲染上下文已经设置好。
你可以安全地在这里调用 ImGui 的渲染函数 ImGui_ImplDX9_RenderDrawData()。
如果在 Present() 之后才绘制，可能会被游戏自身的渲染覆盖。
Hook 成熟且兼容性好：
在很多游戏注入 / 辅助工具中，EndScene Hook 是最常用的方式。
它不破坏游戏逻辑，只是附加渲染。
IDirect3DDevice9的原型是：HRESULT IDirect3DDevice9::EndScene();
流程理解：
开始一帧：
游戏先调用 Clear() 清空渲染目标（颜色缓冲/深度缓冲）。
然后调用 BeginScene() 开始渲染。
游戏绘制：
游戏内部调用 DrawPrimitive、DrawIndexedPrimitive 等方法，渲染模型、特效、UI 等。
结束渲染：
EndScene() 被调用：
Direct3D 告诉 GPU 本帧渲染操作完成。
内部进行一些状态校验和提交命令。
并准备好调用 Present() 时，把渲染结果显示到屏幕。
Present 显示：
IDirect3DDevice9:😛resent() 把后台缓冲区（BackBuffer）内容显示到前台（屏幕）。
这个时候 EndScene 已经确定渲染数据完整。
值得注意的是：
EndScene 是每帧渲染的结束信号，在这里绘制不会被游戏本身渲染覆盖，而且每帧都调用，非常适合插入 ImGui 或外挂显示
。EndScene = 每帧渲染的最后一步，Hook 它能保证每帧都能绘制，渲染上下文安全，不破坏游戏原有逻辑，因此他是我们的首选函数。
我再下面提供一个可供参考的基础代码：
值得拉出来特别说明的是：
extern IMGUI_IMPL_API LRESULT ImGui_ImplWin32_WndProcHandler(HWND hWnd, UINT msg, WPARAM wParam, LPARAM lParam);
我们的窗口除了需要显示一些内容之外，也需要做一些交互，而imgui提供了鼠标键盘灯外部设备交互的接口：
ImGui_ImplWin32_WndProcHandler
，
ImGui::CreateContext();
但是这个函数无法直接使用，在imgui自己的函数说明中有如下信息:
// Win32 message handler (process Win32 mouse/keyboard inputs, etc.)
// Call from your application's message handler. Keep calling your message handler unless this function returns TRUE.
// When implementing your own backend, you can read the io.WantCaptureMouse, io.WantCaptureKeyboard flags to tell if Dear ImGui wants to use your inputs.
// - When io.WantCaptureMouse is true, do not dispatch mouse input data to your main application, or clear/overwrite your copy of the mouse data.
// - When io.WantCaptureKeyboard is true, do not dispatch keyboard input data to your main application, or clear/overwrite your copy of the keyboard data.
// Generally you may always pass all inputs to Dear ImGui, and hide them from your application based on those two flags.
// PS: We treat DBLCLK messages as regular mouse down messages, so this code will work on windows classes that have the CS_DBLCLKS flag set. Our own example app code doesn't set this flag.

// Copy either line into your .cpp file to forward declare the function:
extern IMGUI_IMPL_API LRESULT ImGui_ImplWin32_WndProcHandler(HWND hWnd, UINT msg, WPARAM wParam, LPARAM lParam);                // Use ImGui::GetCurrentContext()
extern IMGUI_IMPL_API LRESULT ImGui_ImplWin32_WndProcHandlerEx(HWND hWnd, UINT msg, WPARAM wParam, LPARAM lParam, ImGuiIO& io); // Doesn't use ImGui::GetCurrentContext()
翻译一下：
// Win32 消息处理函数（处理 Win32 的鼠标/键盘输入等）
// 从你应用程序的消息处理函数中调用它。除非该函数返回 TRUE，否则仍然继续调用你的原始消息处理函数。
// 如果你实现自己的后端，可以读取 io.WantCaptureMouse 和 io.WantCaptureKeyboard 标志，判断 Dear ImGui 是否想要使用你的输入。
// - 当 io.WantCaptureMouse 为 true 时，不要将鼠标输入数据传递给你的主程序，也不要清空或覆盖你自己保存的鼠标数据。
// - 当 io.WantCaptureKeyboard 为 true 时，不要将键盘输入数据传递给你的主程序，也不要清空或覆盖你自己保存的键盘数据。
// 通常，你可以将所有输入都传递给 Dear ImGui，然后根据这两个标志决定是否在你的应用程序中隐藏这些输入。
// PS：我们将双击（DBLCLK）消息视为普通鼠标按下消息，因此这段代码在启用了 CS_DBLCLKS 标志的 Windows 窗口类中也能正常工作。我们自己的示例程序没有设置这个标志。

// 将以下任意一行复制到你的 .cpp 文件中即可前向声明该函数：
extern IMGUI_IMPL_API LRESULT ImGui_ImplWin32_WndProcHandler(HWND hWnd, UINT msg, WPARAM wParam, LPARAM lParam);                // Use ImGui::GetCurrentContext()
extern IMGUI_IMPL_API LRESULT ImGui_ImplWin32_WndProcHandlerEx(HWND hWnd, UINT msg, WPARAM wParam, LPARAM lParam, ImGuiIO& io); // Doesn't use ImGui::GetCurrentContext()
把这些代码复制一下就好了，拿去测试
extern IMGUI_IMPL_API LRESULT ImGui_ImplWin32_WndProcHandler(HWND hWnd, UINT msg, WPARAM wParam, LPARAM lParam);

// ----------------- 全局变量 -----------------
typedef HRESULT(__stdcall* EndScene_t)(LPDIRECT3DDEVICE9 pDevice);
EndScene_t oEndScene = nullptr;

HWND g_hWnd = nullptr;
LPDIRECT3DDEVICE9 g_pd3dDevice = nullptr;
bool g_ImGuiInitialized = false;

WNDPROC oWndProc = nullptr;

// ----------------- WndProc Hook -----------------
LRESULT CALLBACK WndProc(HWND hWnd, UINT msg, WPARAM wParam, LPARAM lParam)
{
    if (g_ImGuiInitialized)
    {
        // 调用官方 Win32 后端处理输入
        ImGui_ImplWin32_WndProcHandler(hWnd, msg, wParam, lParam);
    }

    return CallWindowProc(oWndProc, hWnd, msg, wParam, lParam);
}

void HookWndProc()
{
    g_hWnd = FindWindow(NULL, L"xxxxxxxx"); // 替换为游戏窗口标题
    if (!g_hWnd) g_hWnd = GetForegroundWindow();

    if (g_hWnd)
        oWndProc = (WNDPROC)SetWindowLongPtr(g_hWnd, GWLP_WNDPROC, (LONG_PTR)WndProc);
}

// ----------------- EndScene Hook -----------------

HRESULT __stdcall hkEndScene(LPDIRECT3DDEVICE9 pDevice)
{
    if (!g_ImGuiInitialized)
    {
        g_pd3dDevice = pDevice;
        HookWndProc();

        ImGui::CreateContext();

        ImGuiIO& io = ImGui::GetIO();
        io.Fonts->AddFontFromFileTTF(
            "C:\\Windows\\Fonts\\msyh.ttc",
            16.0f, nullptr, io.Fonts->GetGlyphRangesChineseFull());

        ImGui_ImplWin32_Init(g_hWnd);
        ImGui_ImplDX9_Init(pDevice);
        g_ImGuiInitialized = true;
    }

    // 开始新 Frame
    ImGui_ImplDX9_NewFrame();
    ImGui_ImplWin32_NewFrame();
    ImGui::NewFrame();

    // 设置窗口初始大小和折叠状态 (只在第一次生效)
    ImGui::SetNextWindowSize(ImVec2(300, 200), ImGuiCond_Once);
    ImGui::SetNextWindowCollapsed(true, ImGuiCond_Once);

    // 主窗口
    ImGui::Begin(u8"测试窗口");
   
    ImGui::Text(u8"一个text窗口，随便写点什么:");

    ImGui::End();

    ImGui::EndFrame();
    ImGui::Render();
    ImGui_ImplDX9_RenderDrawData(ImGui::GetDrawData());

    return oEndScene(pDevice);
}

// ----------------- Hook EndScene -----------------
void HookEndScene()
{
    IDirect3D9* pD3D = Direct3DCreate9(D3D_SDK_VERSION);
    if (!pD3D) return;

    D3DPRESENT_PARAMETERS d3dpp = {};
    d3dpp.Windowed = TRUE;
    d3dpp.SwapEffect = D3DSWAPEFFECT_DISCARD;
    d3dpp.hDeviceWindow = GetForegroundWindow();

    LPDIRECT3DDEVICE9 pDummyDevice = nullptr;
    if (SUCCEEDED(pD3D->CreateDevice(D3DADAPTER_DEFAULT, D3DDEVTYPE_HAL, d3dpp.hDeviceWindow,
        D3DCREATE_SOFTWARE_VERTEXPROCESSING, &d3dpp, &pDummyDevice)))
    {
        void** pVTable = *reinterpret_cast<void***>(pDummyDevice);
        oEndScene = (EndScene_t)pVTable[42]; // EndScene 索引

        DetourTransactionBegin();
        DetourUpdateThread(GetCurrentThread());
        DetourAttach(&(PVOID&)oEndScene, hkEndScene);
        DetourTransactionCommit();

        pDummyDevice->Release();
    }
    pD3D->Release();
}
对于上面的代码，其中
WndProc Hook()
，这是自定义的窗口消息处理函数，如果 ImGui 已初始化，就把消息传给 ImGui 后端处理（鼠标/键盘输入），然后调用原始 WndProc，让游戏自己继续处理消息。
输入注入的关键是：
HookWndProc()
，
HookWndProc()
：实际安装 Hook，
FindWindow
找到游戏窗口。
SetWindowLongPtr
替换原始 WndProc，返回原 WndProc 存到
oWndProc
。
所以在
HookWndProc()
这个函数中，记得把
FindWindow(NULL, L"xxxxxxxx");
替换为自己的游戏名字。
EndScene()
这个函数会在游戏每一帧的
EndScene
被调用，这里也是我们做的UI，也就是说所有的页面逻辑可以在这里面搞来搞去，做一些自定义的相关工作。值得一提的是，如果你根本就不懂imgui，那么你可以去简单学习一下，如果完全不感兴趣也没关系，你不需要关注其他的代码，你只需要基本的了解到你的ui控件画在了哪里，要干什么即可：
ImGui_ImplDX9_NewFrame();
ImGui_ImplWin32_NewFrame();
ImGui::NewFrame();
{
	
	//干一些你想干的事情
}
ImGui::EndFrame();
ImGui::Render();
ImGui_ImplDX9_RenderDrawData(ImGui::GetDrawData());
ImGui_ImplDX9_NewFrame();ImGui_ImplWin32_NewFrame();ImGui::NewFrame();
这三行代码开启了新的一帧渲染，
NewFrame()
相当于告诉 ImGui：“准备开始绘制新的一帧界面了”。这之后，你可以创建窗口、控件、布局等。
ImGui::EndFrame();  ImGui::Render();  ImGui_ImplDX9_RenderDrawData(ImGui::GetDrawData());
EndFrame()
告诉 ImGui 这一帧所有窗口和控件已经创建完毕。
Render()
生成渲染命令。
ImGui_ImplDX9_RenderDrawData()
实际调用 DirectX 绘制这些控件。
然后中间的
ImGui::Begin("窗口标题"); ImGui::Text("文本"); ImGui::Button("按钮"); ImGui::End();
都是你的UI了
简单总结一下：
ImGui::NewFrame()
→ 开始新帧
ImGui::Begin()
…
ImGui::End()
→ 放置窗口和控件逻辑
ImGui::EndFrame()
+
ImGui::Render()
→ 渲染生成的 UI
最后就是主程序入口
dllmain()
函数了,很简单，我们创建一个线程，把函数丢进去就行了
DisableThreadLibraryCalls(hModule);
CreateThread(nullptr, 0, (LPTHREAD_START_ROUTINE)HookEndScene, nullptr, 0, nullptr);//IMGUI
把上面的函数丢到
dllmain()
就可以了.
BOOL APIENTRY DllMain(HMODULE hModule, DWORD  ul_reason_for_call, LPVOID lpReserved)
{
	switch (ul_reason_for_call) {
	case DLL_PROCESS_ATTACH:
	{
		CreateConsole();	//console for devs, use this to log stuff if you want

        DisableThreadLibraryCalls(hModule);
        CreateThread(nullptr, 0, (LPTHREAD_START_ROUTINE)HookEndScene, nullptr, 0, nullptr);//IMGUI

		ijl15::CreateHook(); //NMCO::CreateHook();
		
		break;
	}
	default: break;
	case DLL_PROCESS_DETACH:
    {
        if (g_ImGuiInitialized)
        {
            ImGui_ImplDX9_Shutdown();
            ImGui_ImplWin32_Shutdown();
            ImGui::DestroyContext();
        }
        ExitProcess(0);
    }
      
	}
	return TRUE;
}
快去编译尝试一下把！
值得注意的是上面的代码还有一个bug，就是当你切换分辨率的时候，会导致游戏卡死，这点暂未解决
关于上面的问题，在之前发布后就得到解决了，只是未找到合适的发布契机
上面的代码中并没有处理IDrect3DDevice9::Reset，在设备重建时正确销毁/重建 ImGui 对象就可以避免窗口分辨率变化导致的游戏闪退问题,这部分让AI帮忙实现即可
LRESULT CALLBACK WndProc(HWND hWnd, UINT msg, WPARAM wParam, LPARAM lParam)
{
    if (g_ImGuiInitialized)
    {
        ImGui_ImplWin32_WndProcHandler(hWnd, msg, wParam, lParam);
        ImGuiIO& io = ImGui::GetIO();

        // 鼠标进入 ImGui 区域时显示系统光标
        if (msg == WM_SETCURSOR && io.WantCaptureMouse)
        {
            SetCursor(LoadCursor(nullptr, IDC_ARROW));
            return TRUE;
        }

        // 如果 ImGui 想要接管鼠标，则拦截
        if (io.WantCaptureMouse)
        {
            switch (msg)
            {
            case WM_LBUTTONDOWN:
            case WM_LBUTTONUP:
            case WM_RBUTTONDOWN:
            case WM_RBUTTONUP:
            case WM_MBUTTONDOWN:
            case WM_MBUTTONUP:
            case WM_MOUSEWHEEL:
            case WM_MOUSEHWHEEL:
                return TRUE;
            }
        }
    }

    return CallWindowProc(oWndProc, hWnd, msg, wParam, lParam);
}

void HookWndProc()
{
    
    if (!g_pd3dDevice)
        return;

    D3DDEVICE_CREATION_PARAMETERS cparams{};
    if (SUCCEEDED(g_pd3dDevice->GetCreationParameters(&cparams)))
    {
        g_hWnd = cparams.hFocusWindow;
        if (g_hWnd)
            oWndProc = (WNDPROC)SetWindowLongPtr(g_hWnd, GWLP_WNDPROC, (LONG_PTR)WndProc);
    }
}

HRESULT __stdcall hkEndScene(LPDIRECT3DDEVICE9 pDevice)
{
    if (!g_ImGuiInitialized)
    {
        g_pd3dDevice = pDevice;

        D3DDEVICE_CREATION_PARAMETERS cparams{};
        pDevice->GetCreationParameters(&cparams);
        g_hWnd = cparams.hFocusWindow;

        HookWndProc();
        ImGui::CreateContext();
        ImGuiIO& io = ImGui::GetIO();
        io.Fonts->AddFontFromFileTTF(
            "C:\\Windows\\Fonts\\msyh.ttc",
            16.0f,
            nullptr,
            io.Fonts->GetGlyphRangesChineseFull());

        ImGui_ImplWin32_Init(g_hWnd);
        ImGui_ImplDX9_Init(pDevice);
        g_ImGuiInitialized = true;
    }
    else
    {
        // 检查窗口变化并重挂
        D3DDEVICE_CREATION_PARAMETERS cparams{};
        pDevice->GetCreationParameters(&cparams);
        if (cparams.hFocusWindow && cparams.hFocusWindow != g_hWnd)
        {
            if (oWndProc && g_hWnd)
                SetWindowLongPtr(g_hWnd, GWLP_WNDPROC, (LONG_PTR)oWndProc);

            g_hWnd = cparams.hFocusWindow;
            HookWndProc();
            ImGui_ImplWin32_Shutdown();
            ImGui_ImplWin32_Init(g_hWnd);
        }
    }

    if (pDevice->TestCooperativeLevel() != D3D_OK)
        return oEndScene(pDevice);

    // ---------------- 绘制 UI ----------------
    ImGui_ImplDX9_NewFrame();
    ImGui_ImplWin32_NewFrame();
    ImGui::NewFrame();

    // 设置窗口初始大小和折叠状态 (只在第一次生效)
    ImGui::SetNextWindowSize(ImVec2(300, 200), ImGuiCond_Once);
    ImGui::SetNextWindowCollapsed(true, ImGuiCond_Once);

    // 主窗口
    ImGui::Begin(u8"测试窗口");
    ImGui::Text(u8"一个text窗口，随便写点什么:");
    ImGui::End();

    ImGui::EndFrame();
    ImGui::Render();
    ImGui_ImplDX9_RenderDrawData(ImGui::GetDrawData());

    return oEndScene(pDevice);
}

HRESULT __stdcall hkReset(LPDIRECT3DDEVICE9 device, D3DPRESENT_PARAMETERS* pp)
{
    if (g_ImGuiInitialized)
        ImGui_ImplDX9_InvalidateDeviceObjects();

    const HRESULT hr = oReset(device, pp);
    if (SUCCEEDED(hr) && g_ImGuiInitialized)
        ImGui_ImplDX9_CreateDeviceObjects();

    return hr;
}

void HookEndScene()
{
    IDirect3D9* pD3D = Direct3DCreate9(D3D_SDK_VERSION);
    if (!pD3D)
        return;

    D3DPRESENT_PARAMETERS d3dpp{};
    d3dpp.Windowed = TRUE;
    d3dpp.SwapEffect = D3DSWAPEFFECT_DISCARD;
    d3dpp.hDeviceWindow = GetForegroundWindow();

    LPDIRECT3DDEVICE9 pDummyDevice = nullptr;
    if (SUCCEEDED(pD3D->CreateDevice(
            D3DADAPTER_DEFAULT,
            D3DDEVTYPE_HAL,
            d3dpp.hDeviceWindow,
            D3DCREATE_SOFTWARE_VERTEXPROCESSING,
            &d3dpp,
            &pDummyDevice)))
    {
        void** vtable = *reinterpret_cast<void***>(pDummyDevice);
        oEndScene = (EndScene_t)vtable[42];
        oReset = (Reset_t)vtable[16];

        DetourTransactionBegin();
        DetourUpdateThread(GetCurrentThread());
        DetourAttach(&(PVOID&)oEndScene, hkEndScene);
        DetourAttach(&(PVOID&)oReset, hkReset);
        DetourTransactionCommit();

        pDummyDevice->Release();
    }

    pD3D->Release();
}

---

**#2楼**

现在好像越来越多人倾向于用游戏原生组件来绘制自己想要的 UI，而不是使用 ImGui。
但我个人觉得，原生组件方案虽然视觉上更贴近游戏本体，但也有两个明显问题：第一是实现难度比较高，需要理解游戏内部 UI 结构；第二是侵入性更强，容易对游戏原有逻辑造成影响。
ImGui 的优势在于它和游戏本体相对解耦，UI 层基本可以独立维护，不需要大范围修改游戏原生组件。真正需要和游戏交互的部分，也可以通过收发包或其他桥接方式完成。
至于视觉效果，如果能通过其他方式读取游戏资源（*.img，这个有空我会单独分享），然后绘制到 ImGui 表面，其实也可以做出比较接近原生 UI 的效果。

---

**#3楼**

plshelloworld
实际上imgui读了ui之后 基本没差别 做原生太费劲了 尤其是东西越多 代码越多  除非是改造原有的窗体 比如添加一些控件 类似于游戏设置 系统设置这种 适合用原生方式来实现 新东西还是imgui更好

---

**#4楼**

disguisebilly
原生窗口最大的问题就是太臃肿了，耦合性很高，而且维护起来的门槛很高，稍有不慎会触发问题。 游戏最重要的是稳定性，稳定才能给玩家一个好的体验。  所以我感觉社区最近的方向思路很奇怪，都在鼓捣原生窗口，那种方式我觉得仅仅适合自己琢磨，如果真面向玩家的时候，原生窗口绘制多少有点奇怪。
本来是逆向hook的工作，但是却想走正向开发的模式，思路错了

---

**#5楼**

plshelloworld
用原生API画UI的，已经有很多大佬放出demo了，甚至见到有人吐槽teto不应该把API公开出来。如果原生API真有你说的那么不堪，那些大佬也不会趋之若鹜了。
建议你放一些 imgui 的 demo 让事实说话或者让市场说话。

---

**#6楼**

leevccc
我并没有否定原生窗口的,当然可以用原生窗口做出来很好的功能，对于有足够水平的开发者来说这并不是什么困难的事情。
我的看法有一个前提，建立在原生窗口耦合性较大，维护较困难。  我始终觉得稳定性是最重要的，而提高稳定性，提高可维护性最好的方式就是解耦。
关于imgui的demo 在早些时候做了一个伤害皮肤的交互和选择，最近也看到了社区里有用原生窗口绘制的案例。
其实哪种方式都各有优略，但是从可维护性和解耦方面，Imgui还是存在优势的。

---

**#7楼**

leevccc
当然，现在很多所谓的“开发”，其实已经很大程度依赖 AI 来烧 token 了。以我观察，这个社区里可能有相当一部分人，实际都是让 AI 在承担主要的维护和开发任务。
这本身没什么可诟病的，AI 作为工具确实能提高效率。
但问题在于，如果选择大量 hook 游戏原生组件来实现 UI，那么项目复杂度会迅速上升。随着功能模块越来越多，代码会和游戏本体耦合得越来越深，后续维护、排错和兼容都会变得非常困难。
相比之下，ImGui 的优势在于它和游戏原生 UI 体系相对解耦。它不需要深度改造游戏本身的组件逻辑，也不太容易破坏原有 UI 流程。交互逻辑可以通过封包、内存读取或外部通信等方式完成，把显示层和游戏逻辑层分离开来。
所以我更倾向于认为，ImGui 并不是“低级替代品”，而是一种更安全、更独立、也更容易维护的 UI 实现方案。对于复杂项目来说，解耦本身就是很大的优势。
所以我并不是否定原生组件。原生组件更适合追求高度融合、原生体验的场景；而 ImGui 更适合复杂功能、调试工具、扩展面板这类需要快速迭代、低侵入和高可维护性的场景。两者不是谁完全取代谁，而是取舍方向不同。

---

**#8楼**

leevccc
直白的说，没有AI，或者AI不够高级，Token不够的情况下，在这种逆向的前提下，用原生组件去开发或添加一些功能，当前社区大概有90%以上的人完不成这项工作

---

**#9楼**

leevccc
另一个角度来看待这个事情，原生组件teto开放了V083  V095  那么问题来了，我想要任意一个版本的时候，该怎么办呢？
假如换个游戏呢，之前所做的这些工作实际上是不可持续的。

---

**#10楼**

这一楼里都是大佬在讨论一些我看不懂的东西。
plshelloworld
但是这个伤害皮肤选择看上去真不错！我可以自己用PS画，但我不太懂代码...

---

**#11楼**

plshelloworld
talk waste time, show me your code

---

**#12楼**

plshelloworld
我不否认你的观点。而我的观点是，这只是两套工具，各有千秋，各有好坏。有人愿意使用原生 API 并且愿意分享，这是好事。你觉得 imgui 好，可以把你的代码分享出来，无论是在这里或是DC，这都会有助于鼓动更多的人了解和使用 imgui。原生 API 现在这么多人用就是因为用原生API的群体中愿意分享的人多。
你说了这么多，只有真正了解、用过 imgui 的人能理解你，其他没碰过的人根本不感兴趣。。。当然你对我讲这么多是没意义的，因为我两个都研究过，所以我能理解你说的。

---

**#13楼**

leevccc
嗨呀，并非引战，只是探讨两个技术方向的差异而已

---

**#14楼**

plshelloworld
没有说你引战，而是没必要看到用原生的人多就非要扯一下 imgui。 imgui 是好，但是圈子里分享的人少，用的人少，再好又有什么用，毕竟 PHP 是世界上最好的语言不是 :doge

---

**#15楼**

leevccc
imgui更多的是学了一项新的技术，这项技术在任何地方都可以再拿起来用，多个人技术晋升更有意义

---

**#16楼**

leevccc
这就是很遗憾的事情，所以我才说路线有点偏。为啥不加强建设imgui 这种可以通用于各个版本的方向，而是在某个版本原生UI那里猛猛下功夫。

---

**#17楼**

非常细致的教程！对于新手来说无论是原生还是HOOK  先成功实现自己想要的功能带来的正反馈是最重要的

---

**#18楼**

plshelloworld
是的 大家做的都是做的"强行二次开发" 目的实际上是一样的 技术实现不同罢了 原生也好 imgui也好 本质上都只是一种实现方式 只要能实现就好 各有利弊。
说白了还是cpp写ui化的东西太复杂了，imgui给大家提供了一个更便捷的方式，imgui+ms api读wz资源的方式来做窗体 实际上是替换掉了cpp臃肿的ui实现方式 确实更利于维护
从技术实现的角度上来说就是这样的 没有好坏 能实现就好。