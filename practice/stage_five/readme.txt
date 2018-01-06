XYZReader		= основная версия

XYZReaderF		= добавлен ViewPager

XYZReaderG_004	= ViewPager
					= TextView  load
					= bottom popup menu
					= Handler ProgressBar setup
					= ВНИМАНИЕ.  Здесь установлено что Handler позволяет в Background задавать textView		
					
XYZReaderG					= bottom popup menu доработан
								= viewpager visible постоянно
								= progress bar different for text and image
									= glide listener
								= recycler individual progress bars for item image
									= glide listener


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
					= через две Activity to Fragment  
					= RecyclerView to ViewPager и обратно
					= Animation after Transition
						= ВНИМАНИЕ. Обязательно ПРОВЕРЯТЬ на null объект так как вызывается на входе и выходе Activity.
					= убран метод Adapter.hasStableId()
						=ВНИМАНИЕ. Это метод задан по ошибке, используется только для оптимизации лишних notifyDataChanged()
									ПОРТИТ RecyclerView при этом идет неверное заполнение ViewHolder,
									ИСПОЛЬЗОВАТЬ ТОЛЬКО с методом getItemId() который возвращает position.
									
RViewPagerE		= версия RViewPagerD без  callback для Glide
					= прочитать https://guides.codepath.com/android/shared-element-activity-transition
					= возможно они сделаны для большей оптимизации 
					
RViewPagerF		= версия RViewPagerD оптимизация для Glide