/*
 * Copyright (C) 2010 Geometer Plus <contact@geometerplus.com>
 *
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA
 * 02110-1301, USA.
 */

package org.geometerplus.android.fbreader.network;

import android.app.Dialog;
import android.app.AlertDialog;
import android.view.View;
import android.widget.TextView;
import android.content.DialogInterface;

import org.geometerplus.zlibrary.ui.android.R;

import org.geometerplus.zlibrary.ui.android.dialogs.ZLAndroidDialogManager;

import org.geometerplus.zlibrary.core.util.ZLBoolean3;

import org.geometerplus.fbreader.network.authentication.NetworkAuthenticationManager;


class AuthenticationDialog extends NetworkDialog {

	public AuthenticationDialog() {
		super("AuthenticationDialog");
	}

	@Override
	protected void clearData() {
	}

	@Override
	public View createLayout() {
		final View layout = myActivity.getLayoutInflater().inflate(R.layout.network_authentication_dialog, null);

		((TextView) layout.findViewById(R.id.network_authentication_login_text)).setText(myResource.getResource("login").getValue());
		((TextView) layout.findViewById(R.id.network_authentication_password_text)).setText(myResource.getResource("password").getValue());

		final TextView registerText = (TextView) layout.findViewById(R.id.network_authentication_register);
		registerText.setText(myResource.getResource("register").getValue());
		registerText.setOnClickListener(new View.OnClickListener() {
			public void onClick(View view) {
				final NetworkAuthenticationManager mgr = myLink.authenticationManager();
				if (mgr.registrationSupported()) {
					myActivity.dismissDialog(NetworkDialog.DIALOG_AUTHENTICATION);
					NetworkDialog.show(myActivity, NetworkDialog.DIALOG_REGISTER_USER, myLink, new Runnable() {
						public void run() {
							if (mgr.isAuthorised(true).Status == ZLBoolean3.B3_TRUE) {
								if (myOnSuccessRunnable != null) {
									myOnSuccessRunnable.run();
								}
							} else {
								NetworkDialog.show(myActivity, NetworkDialog.DIALOG_AUTHENTICATION, myLink, myOnSuccessRunnable);
							}
						}
					});
				}
			}
		});
		return layout;
	}

	@Override
	protected void onPositive(DialogInterface dialog) {
		AlertDialog alert = (AlertDialog) dialog;
		final String login = ((TextView) alert.findViewById(R.id.network_authentication_login)).getText().toString().trim();
		final String password = ((TextView) alert.findViewById(R.id.network_authentication_password)).getText().toString();

		if (login.length() == 0) {
			final String err = myResource.getResource("loginIsEmpty").getValue();
			sendError(true, false, err);
			return;
		}

		final NetworkAuthenticationManager mgr = myLink.authenticationManager();
		mgr.UserNameOption.setValue(login);
		final Runnable runnable = new Runnable() {
			public void run() {
				String err = mgr.authorise(password);
				if (err != null) {
					mgr.logOut();
					sendError(true, false, err);
					return;
				}
				if (mgr.needsInitialization()) {
					err = mgr.initialize();
					if (err != null) {
						mgr.logOut();
						sendError(true, false, err);
						return;
					}
				}
				sendSuccess(false);
			}
		};
		((ZLAndroidDialogManager)ZLAndroidDialogManager.Instance()).wait("authentication", runnable, myActivity);
	}

	@Override
	protected void onNegative(DialogInterface dialog) {
		final NetworkAuthenticationManager mgr = myLink.authenticationManager();
		final Runnable runnable = new Runnable() {
			public void run() {
				if (mgr.isAuthorised(false).Status != ZLBoolean3.B3_FALSE) {
					mgr.logOut();
					sendCancel(false);
				}
			}
		};
		((ZLAndroidDialogManager)ZLAndroidDialogManager.Instance()).wait("signOut", runnable, myActivity);
	}

	@Override
	public void prepareDialog(Dialog dialog) {
		final NetworkAuthenticationManager mgr = myLink.authenticationManager();

		((TextView) dialog.findViewById(R.id.network_authentication_login)).setText(mgr.UserNameOption.getValue());
		((TextView) dialog.findViewById(R.id.network_authentication_password)).setText("");

		final TextView error = (TextView) dialog.findViewById(R.id.network_authentication_error);
		if (myErrorMessage == null) {
			error.setVisibility(View.GONE);
			error.setText("");
		} else {
			error.setVisibility(View.VISIBLE);
			error.setText(myErrorMessage);
		}

		dialog.findViewById(R.id.network_authentication_register).setVisibility(mgr.registrationSupported() ? View.VISIBLE : View.GONE);

		View dlgView = dialog.findViewById(R.id.network_authentication_dialog);
		dlgView.invalidate();
		dlgView.requestLayout();
	}
}
