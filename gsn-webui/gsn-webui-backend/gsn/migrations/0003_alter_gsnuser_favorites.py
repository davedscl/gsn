# Generated by Django 4.2.2 on 2023-06-15 13:32

from django.db import migrations
import jsonfield.fields


class Migration(migrations.Migration):

    dependencies = [
        ('gsn', '0002_alter_gsnuser_first_name_alter_gsnuser_last_name_and_more'),
    ]

    operations = [
        migrations.AlterField(
            model_name='gsnuser',
            name='favorites',
            field=jsonfield.fields.JSONField(),
        ),
    ]
