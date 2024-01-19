from django.db import models
from django.contrib.auth.models import AbstractUser
from jsonfield import JSONField


class GSNUser(AbstractUser):
    access_token = models.CharField(max_length=100, null=True, blank=True)
    refresh_token = models.CharField(max_length=100, null=True, blank=True)
    token_created_date = models.DateTimeField(null=True, blank=True)
    token_expire_date = models.DateTimeField(null=True, blank=True)
    favorites = JSONField()

    def serialize(self):
        serialized_data = {
            'id': self.id,
            'username': self.username,
            'access_token': self.access_token,
            'refresh_token': self.refresh_token,
            'token_created_date': self.token_created_date,
            'token_expire_date': self.token_expire_date,
            'logged_in': True
        }
        return serialized_data
