// stdafx.h : include file for standard system include files,
// or project specific include files that are used frequently, but
// are changed infrequently
//

#pragma once

#pragma comment(linker, "/nodefaultlib:libc.lib")
#pragma comment(linker, "/nodefaultlib:libcd.lib")

// NOTE - this value is not strongly correlated to the Windows CE OS version being targeted
#define WINVER _WIN32_WCE

#include <ceconfig.h>
#if defined(WIN32_PLATFORM_PSPC) || defined(WIN32_PLATFORM_WFSP)
#define SHELL_AYGSHELL
#endif

#ifdef _CE_DCOM
#define _ATL_APARTMENT_THREADED
#endif

#include <aygshell.h>
#pragma comment(lib, "aygshell.lib") 

#define _ATL_CSTRING_EXPLICIT_CONSTRUCTORS	// some CString constructors will be explicit
#include <atlbase.h>

// Windows Header Files:
#include <windows.h>



#if _WIN32_WCE < 0x500 && ( defined(WIN32_PLATFORM_PSPC) || defined(WIN32_PLATFORM_WFSP) )
	#pragma comment(lib, "ccrtrtti.lib")
	#ifdef _X86_	
		#if defined(_DEBUG)
			#pragma comment(lib, "libcmtx86d.lib")
		#else
			#pragma comment(lib, "libcmtx86.lib")
		#endif
	#endif
#endif

//#include <altcecrt.h>

// TODO: reference additional headers your program requires here
#include <atlapp.h>

extern CAppModule _Module;

#include <atlwin.h>
#include <atlscrl.h>
#include <atlmisc.h>
#include <atlddx.h>
#include <atlcrack.h>
#include <atlctrls.h>
#include <atlframe.h>
#define _WTL_NEW_PAGE_NOTIFY_HANDLERS
#include <atldlgs.h>