section	.text
   global _start    ;must be declared for using gcc

_start:             ;tell linker entry point
   mov	ax,'8'
   sub     ax, '0'

   mov 	bl, '2'
   sub     bl, '0'
   div 	bl
   add	ax, '0'

   mov 	[res], ax
   mov	ecx,msg
   mov	edx, len
   mov	ebx,1	;file descriptor (stdout)
   mov	eax,4	;system call number (sys_write)
