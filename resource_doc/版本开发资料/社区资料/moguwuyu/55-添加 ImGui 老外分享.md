# 添加 ImGui 老外分享

> 来源：https://moguwuyu.com/d/55
> 站点：蘑菇物语(moguwuyu.com) · Flarum 改端技术论坛

**#1楼**

原文地址:
https://forum.playkuro.com/showthread.php?tid=28
OVERVIEW - D3D8 TO D3D9 CONVERSION WITH IMGUI
This guide explains how to integrate Dear ImGui into legacy Direct3D 8 games by using a D3D8 to D3D9 wrapper. This technique allows modern UI overlays in games that still use the deprecated D3D8 API.
Useful Resources:
d3d8to9 by crosire - The original D3D8 to D3D9 wrapper
Dear ImGui - Immediate mode GUI library
Microsoft Detours - API hooking library
ARCHITECTURE OVERVIEW
The integration consists of 4 main components:
D3D8to9 Wrapper - Translates D3D8 API calls to D3D9
API Hooking Layer - Intercepts Direct3D creation and device methods
ImGui Integration - Renders UI using the wrapped D3D9 device
Input Handling - Captures mouse/keyboard for UI interaction
STEP 1: D3D8TO9 WRAPPER SETUP
Required Files:
d3d8to9.cpp - Main wrapper entry point
d3d8to9.hpp - Interface definitions
d3d8to9_device.cpp - Device implementation
d3d8types.hpp - Type conversions
Additional implementation files for resources
Key Components:
// d3d8to9.cpp - Export the Direct3DCreate8 function
代码登录后可见
// d3d8to9.hpp - Wrapper class structure
代码登录后可见
// Example of parameter conversion in CreateDevice
代码登录后可见
STEP 2: HOOKING INFRASTRUCTURE
Create D3D8to9Hook.cpp:
代码登录后可见
// Alternative approach: Hook by replacing the export in the DLL
// This is useful if you're creating a replacement d3d8.dll
代码登录后可见
Alternative: DLL Replacement Method
If you prefer to replace d3d8.dll entirely, create a module definition file:
; d3d8.def - Module definition for d3d8.dll replacement
LIBRARY d3d8
EXPORTS
Direct3DCreate8 @1 NONAME
DebugSetMute @2 NONAME
ValidatePixelShader @3 NONAME
ValidateVertexShader @4 NONAME
Then in your DLL project settings, reference this .def file to ensure proper exports.
STEP 3: IMGUI D3D9 HOOK SETUP
Create SimpleD3D9ImGuiHook.cpp:
代码登录后可见
STEP 4: INPUT HANDLING
Hook Window Procedure:
WNDPROC oWndProc;
代码登录后可见
STEP 5: DLL ENTRY POINT
dllmain.cpp:
代码登录后可见
REQUIRED DEPENDENCIES
GitHub Repositories:
Microsoft Detours - API hooking library
Dear ImGui - Immediate mode GUI library
d3d8to9 - D3D8 to D3D9 wrapper reference
DirectX Graphics Samples - Official DirectX examples
Required Files to Download:
Microsoft Detours:
detours.lib (from build or NuGet package)
detours.h, detver.h (headers)
Dear ImGui:
imgui.cpp, imgui.h (core)
imgui_demo.cpp, imgui_draw.cpp, imgui_tables.cpp, imgui_widgets.cpp
imgui_impl_win32.cpp, imgui_impl_win32.h (Win32 backend)
imgui_impl_dx9.cpp, imgui_impl_dx9.h (DirectX 9 backend)
DirectX 9 SDK:
d3d9.lib, d3dx9.lib (link libraries)
d3d9.h, d3dx9.h (headers)
Runtime Requirements:
d3d9.dll - DirectX 9 runtime (Windows system)
d3dx9_43.dll - D3DX9 helper library (download)
Your compiled DLL with d3d8.dll exports
NuGet Packages (Visual Studio):
<!-- Add to your .vcxproj or use Package Manager -->
<PackageReference Include="Microsoft.Detours" Version="4.0.1" />
<PackageReference Include="directxtk" Version="2024.2.8.1" />
BUILD CONFIGURATION
Visual Studio Project Settings:
代码登录后可见
Module Definition File (d3d8.def):
LIBRARY d3d8
EXPORTS
Direct3DCreate8 @1
COMMON ISSUES AND SOLUTIONS
Problem: Game crashes on startup
Check d3dx9_43.dll is present
Verify hook timing - some games need delayed initialization
Use SEH to catch exceptions during device creation
Problem: ImGui not rendering
Ensure EndScene is being called by the game
Check if device is using pure device flag
Verify state blocks are properly saved/restored
Problem: Input not working
Make sure window handle is correct
Check if game uses DirectInput (needs separate handling)
Verify WndProc hook is installed after window creation
ADVANCED FEATURES
Multi-threaded Rendering:
// Use critical section for thread safety
CRITICAL_SECTION g_cs;
InitializeCriticalSection(&g_cs);
代码登录后可见
EXAMPLE PROJECT STRUCTURE
YourProject/
├── src/
│  ├── d3d8to9/
│  │  ├── d3d8to9.cpp
│  │  ├── d3d8to9.hpp
│  │  ├── d3d8to9_device.cpp
│  │  └── d3d8types.hpp
│  ├── hooks/
│  │  ├── D3D8to9Hook.cpp
│  │  └── SimpleD3D9ImGuiHook.cpp
│  ├── imgui/
│  │  ├── imgui.cpp
│  │  ├── imgui.h
│  │  ├── imgui_impl_win32.cpp
│  │  ├── imgui_impl_win32.h
│  │  ├── imgui_impl_dx9.cpp
│  │  └── imgui_impl_dx9.h
│  └── dllmain.cpp
├── lib/
│  ├── detours.lib
│  ├── d3d9.lib
│  └── d3dx9.lib
├── include/
│  ├── detours.h
│  ├── d3d9.h
│  └── d3dx9.h
├── d3d8.def
└── YourProject.vcxproj
DEBUGGING TIPS
Use OutputDebugString for logging:
// Add this to track initialization
OutputDebugStringA("D3D8to9: Direct3DCreate8 called\n");
OutputDebugStringA("ImGui: EndScene hook called\n");
// Use DebugView from Sysinternals to see output
// Download:
https://docs.microsoft.com/en-us/sysinte.../debugview
Common vtable indices for D3D9:
// IDirect3DDevice9 vtable indices
// 0  - QueryInterface
// 1  - AddRef
// 2  - Release
// 16 - Reset
// 42 - EndScene
// 43 - Present
// Verify with:
void** vtable =
(void
**)pDevice;
OutputDebugStringA("EndScene address: 0x%p\n", vtable[42]);
Useful GitHub Examples:
ImGui DirectX Hook Example
Kiero - Universal graphics hook
CONCLUSION
This architecture allows seamless integration of modern UI frameworks into legacy D3D8 applications. The game continues using D3D8 APIs while ImGui renders through the underlying D3D9 device, providing the best of both worlds - compatibility and modern features.
Key benefits:
No game code modification required
Modern UI capabilities in legacy games
Transparent D3D8 to D3D9 conversion
Full ImGui feature set available
Minimal performance impact
Remember to handle edge cases specific to your target game, test thoroughly, and always backup original game files!
Guide created based on Ezorsia V2 MapleStory V83 client implementation