
# برنامه محاسبه اعداد اول به صورت توزیع شده

## الگوریتم محاسبه :‌
برای اینکه محاسبه کنیم عدد x اول است یا نه ، بایستی آن را بر اعداد اول ۲ تا رادیکال آن عدد تقسیم کنیم، که اگر بر هیچ کدام بخش پذیر نباشد،‌ درنتیجه اول خواهد بود. برای محاسبه به صورت توزیع شده ، به هر کلاینت بازه ای از این اعداد اول را اختصاص می دهیم و هر کلاینت مسئول انجام تقسیم عدد مورد نظر بر اعداد اولی است که به او اختصاص یافته است و در نهایت سرور باتوجه به اطلاعات دریافتی از کلاینت ها تصمیم می گیرد که عدد موردنظر اول است یا نه. بدین صورت که هر کلاینت تعدادی از اعداد اول را در خود نگه می دارد و شروع به تقسیم عدد مورد نظر به اعداد اول درون بازه ای که به او اختصاص یافته می کند. اگر هیچ باقیمانده صفری ظاهر نشود، به سرور می گوید که عدد مورد نظر نسبت به اعداد اولی که او دارد اول است،‌ اما اگر بر یک عدد بخش پذیر باشد،‌ کلاینت محاسبه را متوقف کرده و به سرور اطلاع می دهد که عدد مورد نظر اول نیست . در نتیجه سرور هم به تمام کلاینت های متصل خبر می دهد که لازم به ادامه محاسبه نیست و سراغ محاسبه عدد بعدی (x+1 ) می رود. اما اگر همه کلاینت ها اعلام کنند که عدد مورد نظر اول است (هر کس نسبت به بازه خود) در نتیجه سرور آن عدد را به لیست اعداد اول خود اضافه می کند و در فایل log.txt هم ذخیره می کند و این عدد به یکی از کلاینت ها (کلاینتی که تعداد اعداد اولی که دارد کمتر از بقیه است) فرستاده میشود و کلاینت مورد نظر آن عدد را به لیست اعداد اول خودش اضافه می کند.

## اضافه شدن کلاینت جدید و قطع شدن ارتباط یک کلاینت
در صورتی که یک کلاینت حین محاسبه از سرور جدا شود،‌ این مشکل هندل شده و عددی که حین محاسبه آن این اتفاق افتاده دوباره محاسبه شده و همچنین سرور اطلاعات کامل کلاینت ها را دارد. یعنی می داند که هر کلاینت چه اعداد اولی را در خود نگه می دارد. پس با قطع کلاینت مورد نظر، سرور پیش از شروع محاسبه عدد بعدی،‌ اعداد اول کلاینت قطع شده را بین کلاینت های موجود تقسیم می کند و برای آنها می فرستد. سیستم تا جایی که حداقل یک کلاینت به آن وصل باشد،‌ به محاسبه اعداد اول می پردازد و اگر همه کلاینت ها قطغ شوند،‌ منتظر اتصال کلاینت می ماند و با اتصال کلاینت محاسبه را ادامه می دهد. همچنین با متصل شدن یک کلاینت جدید به سرور، بلافاصله در فرآیند محاسبه قرار میگیرد.


## مشکل از دست رفتن داده در صورت Down شدن سرور
تمام اعداد اولی که محاسبه می شوند در فایل log.txt ذخیره می شود. بدین ترتیب اگر سرور از کار بیفتد،‌ میتوان با اجرا کردن دوباره سرور و دادن فایل log به آن محاسبه را ادامه داد.
همچنین اعداد اول محاسبه شده روی کلاینت ها هم ذخیره می شود و امکان پیاده سازی این موضوع وجود دارد که سرور بعد از اجرا شدن با اتصال به کلاینت ها اعداد اولش را آپدیت کند و محاسبه را از آن جا ادامه دهد.


## نحوه اجرا
### سرور
برای اجرای سرور ابتدا باید پورتی که میخواهید روی آن سوکت بزنید را وارد کنید. سپس در صورتی که میخواهید محاسبه را از عدد ۲ شروع کنید گزینه ۱ را وارد کنید و اگر فایل log از قبل دارید گزینه ۲ را انتخاب و مسیر فایل را به آن بدهید. سپس انتهای بازه ای که می خواهید تا آنجا اعداد اول محاسبه شوند را وارد کنید . حال سرور منتظر اتصال کلاینت است.
### کلاینت
با اجرای کلاینت ابتدا ip سرور و سپس port آن را وارد کنید. در نتیجه به سرور متصل شده و محاسبه روی کلاینت شروع می شود.


## داکر
داکرفایل پروژه هم در کنار آن است و با build کردن  وسپس run  کردن آن  میتوان سرور و کلاینت را به صورت داکر اجرا نمود.

تمرین سوم درس تکنولوزی کامپیوتر - دانشگاه صنعتی شریف - بهار ۹۸
محمد جواد شریعتی  - 96100414 - mjshariati98@gmail.com

