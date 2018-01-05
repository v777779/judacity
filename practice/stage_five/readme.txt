RViewPager		= двойной shared component
					=  https://guides.codepath.com/android/shared-element-activity-transition
				= основные моменты
					= назначить исходные View точно оригиналы для каждого перемещаемого объекта
					= назначить попарно имена transition_name
					= сформировать Intent включающий все перемещаемые пары
				= задать style Activity
				= задать  windowRequest(newExplode())
				= FAB окружить FrameLayout но shared сделать именно FAB	
					= нужно чтобы Fab не мигало при выходе
					= сделать ручное гашение FAB при работе с Collapsed
				
				
RViewPagerA		= версия с getWindow() 	методами
RViewPagerB		= версия с styles 		установками

RViewPagerC		= Activity >> Activity >> Fragment transition ok
RViewPagerD		= полный комплект переходов с отслеживанием RecyclerView ViewPager
				